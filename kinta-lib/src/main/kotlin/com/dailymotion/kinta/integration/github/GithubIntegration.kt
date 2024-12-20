package com.dailymotion.kinta.integration.github

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.dailymotion.kinta.GetPullRequestWithBaseQuery
import com.dailymotion.kinta.GetPullRequestWithHeadQuery
import com.dailymotion.kinta.GetRefsQuery
import com.dailymotion.kinta.GitTool
import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.Project
import com.dailymotion.kinta.integration.git.model.BranchInfo
import com.dailymotion.kinta.integration.git.model.PullRequestInfo
import com.dailymotion.kinta.integration.github.internal.GithubOauthClient
import com.damnhandy.uri.template.UriTemplate
import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava
import com.goterl.lazysodium.interfaces.Box
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import java.io.File
import java.util.Base64


@Suppress("NAME_SHADOWING")
object GithubIntegration : GitTool {
    val json = Json {
        ignoreUnknownKeys = true
    }
    override fun openPullRequest(token: String?,
                                 owner: String?,
                                 repo: String?,
                                 head: String?,
                                 base: String?,
                                 title: String?,
                                 body: String?,
    ): String? {
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
                "base" to JsonPrimitive(base),
                "body" to JsonPrimitive(body),
            )
        )

        val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.github.com/repos/$owner/$repo/pulls")
            .post(body)
            .build()

        Logger.i("creating pull request to merge $head into $base")
        val response = httpClient(token).newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body?.string() ?: "")
        }

        return response.body?.charStream()?.let {
            try {
                val htmlUrl = json.parseToJsonElement(it.readText()).jsonObject["html_url"]?.jsonPrimitive?.content
                Logger.i("-> $htmlUrl")
                htmlUrl
            } catch (e: Exception) {
                e.printStackTrace()
                null
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
        remote: String,
        branch: String): BranchInfo {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository(remote).owner
        val repo_ = repo ?: repository(remote).name

        val depdendentPullRequestsData = runBlocking {
            val query = GetPullRequestWithBaseQuery(owner_, repo_, branch)
            apolloClient(token_).query(query)
                .execute()
                .data
                ?.repository
        }

        val pullRequestsData = runBlocking {
            val query = GetPullRequestWithHeadQuery(owner_, repo_, branch)
            apolloClient(token_).query(query)
                .execute()
                .data
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
            throw Exception(response.body?.string() ?: "")
        }

    }

    override fun getAllBranches(
        token: String?,
        owner: String?,
        repo: String?
    ): List<String> {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        return runBlocking {
            val query = GetRefsQuery(owner, repo)
            apolloClient(token).query(query)
                .execute()
                .data
                ?.repository
                ?.refs
                ?.edges
                ?.map { it?.node?.name }
                ?.filterNotNull() ?: emptyList()

        }
    }

    fun apolloClient(token: String): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(httpClient(token))
            .build()
    }

    private fun httpClient(token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
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

    fun repository(remote: String = "origin"): Repository {
        val git = Git(Project.repository)

        /*
         * I did not find another way to retrieve the github repository details than to parse
         * the remote url
         */
        val remoteConfigList = git.remoteList().call()

        val uri = remoteConfigList.filter { it.name == remote }.first().urIs[0]

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
            .post(JsonObject(input).toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .url("https://api.github.com/repos/$owner/$repo/releases")
            .build()

        val response = httpClient(token).newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("cannot create github release: ${response.body?.string()}")
        }

        val responseString = response.body!!.string()

        val release = json.parseToJsonElement(responseString).jsonObject

        assets.forEach { asset ->
            println("uploading ${asset.name}")
            val uploadUrl = UriTemplate.fromTemplate(release["upload_url"]?.jsonPrimitive?.content)
                .set("name", asset.name)
                .expand()

            val request2 = Request.Builder()
                .post(asset.asRequestBody("application/zip".toMediaTypeOrNull()))
                .url(uploadUrl)
                .build()

            val response2 = httpClient(token).newCall(request2).execute()
            check(response2.isSuccessful) {
                "cannot upload asset: ${response2.body?.string()}"
            }
        }
    }

    @Serializable
    class PublicKey(val key_id: String,
                    val key: String)

    @OptIn(ExperimentalSerializationApi::class)
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
            "cannot retrieve key: ${response.body?.byteStream()?.reader()?.readText()}"
        }

        val json = response.body?.byteStream()?.reader()?.readText() ?: ""
        return this.json.decodeFromString(json)
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

        val messageBytes = value.encodeUtf8().toByteArray()

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
            .put(jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .url("https://api.github.com/repos/$owner/$repo/actions/secrets/$name")
            .build()
            .let {
                httpClient(token).newCall(it).execute()
            }

        check(response.isSuccessful) {
            "cannot set secret $name: ${response.body?.byteStream()?.reader()?.readText()}"
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
            "cannot delete secret $name: ${response.body?.byteStream()?.reader()?.readText()}"
        }
    }

    override fun setAssignees(
        token: String?,
        owner: String?,
        repo: String?,
        issue: String,
        assignees: List<String>
    ) {
        val token = token ?: retrieveToken()
        val owner = owner ?: repository().owner
        val repo = repo ?: repository().name

        val jsonObject = JsonObject(
            mapOf("assignees" to JsonArray(assignees.map { JsonPrimitive(it) }))
        )

        val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.github.com/repos/$owner/$repo/issues/$issue/assignees")
            .post(body)
            .build()

        Logger.i("Assigning...")
        val response = httpClient(token).newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body?.string() ?: "")
        }
    }
}