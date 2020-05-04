package com.dailymotion.kinta.integration.github

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import com.dailymotion.kinta.*
import com.dailymotion.kinta.integration.git.model.BranchInfo
import com.dailymotion.kinta.integration.git.model.PullRequestInfo
import com.dailymotion.kinta.integration.github.internal.GithubOauthClient
import com.damnhandy.uri.template.UriTemplate
import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.Box
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.parse
import kotlinx.serialization.toUtf8Bytes
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import java.io.File
import java.util.*


@Suppress("NAME_SHADOWING")
object GithubIntegration : GitTool {
    override fun openPullRequest(token: String?,
                                 owner: String?,
                                 repo: String?,
                                 head: String?,
                                 base: String?,
                                 title: String?
    ) {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        val head = head ?: Project.repository.branch!!
        val base = base ?: "master"
        val title = title ?: head

        check(head != base) {
            "You cannot make a pull request with the same head and base ($head)"
        }

        val jsonObject = JsonObject(
            mapOf(
                "title" to JsonPrimitive(title),
                "head" to JsonPrimitive(head),
                "base" to JsonPrimitive(base)
            )
        )

        val body = RequestBody.create(MediaType.parse("application/json"), Json.Companion.nonstrict.toJson(jsonObject).toString())

        val request = Request.Builder()
            .url("https://api.github.com/repos/$owner/$repo/pulls")
            .post(body)
            .build()

        Logger.i("creating pull request to merge $head into $base")
        val response = httpClient(token).newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body()?.string() ?: "")
        }

        response.body()?.charStream()?.let {
            try {
                val htmlUrl = Json.nonstrict.parseJson(it.readText()).jsonObject.getPrimitive("html_url").content
                Logger.i("-> $htmlUrl")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Get some info about the pull requests associated to the branch
     */
    override fun getBranchInfo(
        token: String?,
        owner: String?,
        repo: String?,
        branch: String): BranchInfo {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        val depdendentPullRequestsData = runBlocking {
            val query = GetPullRequestWithBase(owner_, repo_, branch)
            apolloClient(token_).query(query)
                .toDeferred()
                .await()
                .data()
                ?.repository
        }

        val pullRequestsData = runBlocking {
            val query = GetPullRequestWithHead(owner_, repo_, branch)
            apolloClient(token_).query(query)
                .toDeferred()
                .await()
                .data()
                ?.repository
        }

        return BranchInfo(
            name = branch,
            pullRequests = (pullRequestsData?.pullRequests?.nodes?.mapNotNull {
                it?.let { PullRequestInfo(it.number, it.merged, it.closed) }
            }) ?: listOf(),
            dependentPullRequests = (depdendentPullRequestsData?.pullRequests?.nodes?.mapNotNull {
                it?.let { PullRequestInfo(it.number, it.merged, it.closed) }
            }) ?: listOf()
        )
    }

    override fun deleteRef(
        token: String?,
        owner: String?,
        repo: String?,
        ref: String
    ) {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        println("deleting $ref...")

        val request = Request.Builder()
            .url("https://api.github.com/repos/$owner_/$repo_/git/refs/heads/$ref")
            .delete()
            .build()

        val response = httpClient(token_).newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body()?.string() ?: "")
        }

    }

    override fun isConfigured() = GithubOauthClient.isConfigured()

    override fun getAllBranches(
        token: String?,
        owner: String?,
        repo: String?
    ): List<String> {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        return runBlocking {
            val query = GetRefs(owner, repo)
            apolloClient(token).query(query)
                .toDeferred()
                .await()
                .data()
                ?.repository
                ?.refs
                ?.edges
                ?.map { it?.node?.name }
                ?.filterNotNull() ?: emptyList()

        }
    }

    fun apolloClient(token: String): ApolloClient {
        return ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(httpClient(token))
            .build()
    }

    private fun httpClient(token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${token}")
                    .build()
                )
            }
            .build()
    }

    fun retrieveToken(): String {
        return KintaEnv.get(KintaEnv.Var.GITHUB_TOKEN)
            ?: GithubOauthClient.getToken()
    }

    data class Repository(val owner: String, val name: String)

    fun repository(): Repository {
        val git = Git(Project.repository)

        /*
         * I did not find another way to retrieve the github repository details than to parse
         * the remote url
         */
        val remoteConfigList = git.remoteList().call()

        val uri = remoteConfigList.filter { it.name == "origin" }.first().urIs[0]

        return repoDetails(uri)
    }

    private fun repoDetails(uriIsh: URIish): Repository {
        if (uriIsh.host != "github.com") {
            throw Exception("this script only works with github.com")
        }

        // https://github.com/owner/repo.git has a leading '/'
        // git@github.com:owner/repo.git has not
        val s = uriIsh.path.trim('/').split("/")

        val repoName = s[1].removeSuffix(".git")

        return Repository(s[0], repoName)
    }

    fun createRelease(token: String? = null,
                      owner: String? = null,
                      repo: String? = null,
                      tagName: String,
                      changelogMarkdown: String,
                      assets: List<File>) {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        val input = mapOf(
            "tag_name" to JsonPrimitive(tagName),
            "name" to JsonPrimitive(tagName),
            "body" to JsonPrimitive(changelogMarkdown),
            "draft" to JsonPrimitive(false),
            "prerelease" to JsonPrimitive(false)
        )

        val request = Request.Builder()
            .post(RequestBody.create(MediaType.parse("application/json"), JsonObject(input).toString()))
            .url("https://api.github.com/repos/$owner/$repo/releases?access_token=${token}")
            .build()

        val response = OkHttpClient().newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("cannot create github release: ${response.body()?.string()}")
        }

        val responseString = response.body()!!.string()

        val release = Json.nonstrict.parseJson(responseString)

        assets.forEach { asset ->
            println("uploading ${asset.name}")
            val uploadUrl = UriTemplate.fromTemplate(release.jsonObject.getPrimitive("upload_url").content)
                .set("name", asset.name)
                .expand()

            val request2 = Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/zip"), asset))
                .url("$uploadUrl&access_token=$token")
                .build()

            val response2 = OkHttpClient().newCall(request2).execute()
            check(response2.isSuccessful) {
                "cannot upload asset: ${response2.body()?.string()}"
            }
        }
    }

    @Serializable
    class PublicKey(val key_id: String,
                    val key: String)

    private fun publicKey(
        token: String?,
        owner: String?,
        repo: String?
    ): PublicKey {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        val url = "https://api.github.com/repos/$owner/$repo/actions/secrets/public-key"

        val response = Request.Builder().get().url(url).build()
            .let {
                httpClient(token).newCall(it).execute()
            }

        check(response.isSuccessful) {
            "cannot retrieve key: ${response.body()?.byteStream()?.reader()?.readText()}"
        }

        val json = response.body()?.byteStream()?.reader()?.readText() ?: ""
        return Json.nonstrict.parse<PublicKey>(json)
    }

    fun setSecret(
        token: String? = null,
        owner: String? = null,
        repo: String? = null,
        name: String,
        value: String
    ) {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        val lazySodium = LazySodiumJava(SodiumJava())

        val publicKey = publicKey(token, owner, repo)
        val keyBytes = Base64.getDecoder().decode(publicKey.key)

        val messageBytes = value.toUtf8Bytes()

        val encryptedByteArray = ByteArray(Box.SEALBYTES + messageBytes.size)

        val encryptionResult = lazySodium.cryptoBoxSeal(encryptedByteArray, messageBytes, messageBytes.size.toLong(), keyBytes)

        check(encryptionResult) {
            "Cannot encrypt actions secret"
        }

        val encryptedValue = Base64.getEncoder().encodeToString(encryptedByteArray)
        val jsonObject = JsonObject(mapOf(
            "key_id" to JsonPrimitive(publicKey.key_id),
            "encrypted_value" to JsonPrimitive(encryptedValue)
        ))

        val response = Request.Builder()
            .put(RequestBody.create(MediaType.parse("application/json"), jsonObject.toString()))
            .url("https://api.github.com/repos/$owner/$repo/actions/secrets/$name")
            .build()
            .let {
                httpClient(token).newCall(it).execute()
            }

        check(response.isSuccessful) {
            "cannot set secret $name: ${response.body()?.byteStream()?.reader()?.readText()}"
        }
    }

    fun deleteSecret(
        token: String? = null,
        owner: String? = null,
        repo: String? = null,
        name: String
        ) {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        val response = Request.Builder()
            .delete()
            .url("https://api.github.com/repos/$owner/$repo/actions/secrets/$name")
            .build()
            .let {
                httpClient(token).newCall(it).execute()
            }

        check(response.isSuccessful) {
            "cannot delete secret $name: ${response.body()?.byteStream()?.reader()?.readText()}"
        }
    }
}