package com.dailymotion.kinta.integration.github

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import com.dailymotion.kinta.*
import com.dailymotion.kinta.integration.github.internal.GithubOauthClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish

object GithubIntegration {
    fun openPullRequest(token: String? = null,
                        owner: String? = null,
                        repo: String? = null,
                        head: String? = null,
                        base: String? = null,
                        title: String? = null
    ) {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        val head_ = head ?: Project.repository.branch!!
        val base_ = base ?: "master"
        val title_ = title ?: base_

        check(head_ != base_) {
            "You cannot make a pull request with the same head and base ($head_)"
        }

        val jsonObject = JsonObject(
                mapOf(
                        "title" to JsonPrimitive(title_),
                        "head" to JsonPrimitive(head_),
                        "base" to JsonPrimitive(base_)
                )
        )

        val body = RequestBody.create(MediaType.parse("application/json"), Json.Companion.nonstrict.toJson(jsonObject).toString())

        val request = Request.Builder()
                .url("https://api.github.com/repos/$owner_/$repo_/pulls")
                .post(body)
                .build()

        Log.d("creating pull request to merge $head_ into $base_")
        val response = httpClient(token_).newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body()?.string() ?: "")
        }

        response.body()?.charStream()?.let {
            try {
                val htmlUrl = Json.nonstrict.parseJson(it.readText()).jsonObject.getPrimitive("html_url").content
                Log.d("-> $htmlUrl")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * @param name: the name of the branch
     * @param pullRequests: the pull requests associated with a given branch
     */
    data class BranchInfo(
            val name: String,
            val pullRequests: List<PullRequestInfo>,
            val dependantPullRequests: List<PullRequestInfo>
    )

    data class PullRequestInfo(
            val number: Int,
            val merged: Boolean,
            val closed: Boolean
    )

    /**
     * For each branch in branches, get some info about the pull requests associated to it
     */
    fun getBranchInfo(
            token: String? = null,
            owner: String? = null,
            repo: String? = null,
            branch: String): BranchInfo {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        val branchData = runBlocking {
            val query = GetBranchInfos(owner_, repo_, branch)
            apolloClient(token_).query(query)
                    .toDeferred()
                    .await()
                    .data()
                    ?.repository
        }

        return BranchInfo(
                name = branch,
                pullRequests = (branchData?.ref?.associatedPullRequests?.nodes?.mapNotNull {
                    it?.let { PullRequestInfo(it.number, it.merged, it.closed) }
                }) ?: listOf(),
                dependantPullRequests = (branchData?.pullRequests?.nodes?.mapNotNull {
                    it?.let { PullRequestInfo(it.number, it.merged, it.closed) }
                }) ?: listOf()
        )
    }

    fun deleteRef(
            token: String? = null,
            owner: String? = null,
            repo: String? = null,
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

    fun getAllBranches(
            token: String? = null,
            owner: String? = null,
            repo: String? = null
    ) : List<String>{
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        return runBlocking {
            val query = GetRefs(owner_, repo_)
            apolloClient(token_).query(query)
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
        return KintaEnv.get(KintaEnv.GITHUB_TOKEN)
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

    fun repoDetails(uriIsh: URIish): Repository {
        if (uriIsh.host != "github.com") {
            throw Exception("this script only works with github.com")
        }

        // https://github.com/owner/repo.git has a leading '/'
        // git@github.com:owner/repo.git has not
        val s = uriIsh.path.trim('/').split("/")

        val repoName = s[1].removeSuffix(".git")

        return Repository(s[0], repoName)
    }
}