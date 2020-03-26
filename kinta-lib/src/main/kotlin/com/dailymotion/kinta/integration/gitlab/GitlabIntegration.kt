package com.dailymotion.kinta.integration.gitlab

import com.dailymotion.kinta.GitTool
import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.Project
import com.dailymotion.kinta.integration.git.model.BranchInfo
import com.dailymotion.kinta.integration.git.model.PullRequestInfo
import com.dailymotion.kinta.integration.gitlab.internal.GitlabService
import com.dailymotion.kinta.integration.gitlab.internal.MergeRequestBody
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import retrofit2.Retrofit

object GitlabIntegration : GitTool {

    private const val GITLAB_API_VERSION = "v4"
    private const val GITLAB_API = "https://gitlab.com/api/$GITLAB_API_VERSION/"

    private fun service(token: String?): GitlabService {
        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.Var.GITLAB_PERSONAL_TOKEN)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token_))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(GITLAB_API)
                .client(okHttpClient)
                .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
                .build()

        return retrofit.create(GitlabService::class.java)
    }

    private class AuthInterceptor(val token: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val builder = request.newBuilder()
            builder.addHeader("Authorization", "Bearer $token")
            return chain.proceed(builder.build())
        }
    }

    override fun openPullRequest(token: String?,
                                 owner: String?,
                                 repo: String?,
                                 head: String?,
                                 base: String?,
                                 title: String?
    ) {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name
        val head_ = head ?: Project.repository.branch!!
        val base_ = base ?: "master"
        val title_ = title ?: head_

        check(head_ != base_) {
            "You cannot make a pull request with the same head and base ($head_)"
        }

        Logger.i("creating pull request to merge $head_ into $base_")
        val response = service(token_).openPullRequest(
                projectId = "$owner_/$repo_",
                mergeRequestBody = MergeRequestBody(head_, base_, title_)
        ).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body()?.string() ?: "")
        }

        response.body()?.charStream()?.let {
            try {
                val htmlUrl = Json.nonstrict.parseJson(it.readText()).jsonObject.getPrimitive("web_url").content
                Logger.i("-> $htmlUrl")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * For each branch in branches, get some info about the pull requests associated to it
     */
    override fun getBranchInfo(
            token: String?,
            owner: String?,
            repo: String?,
            branch: String): BranchInfo {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        val response = service(token_).getMRWithHead(projectId = "$owner_/$repo_", source = branch).execute()
        if (!response.isSuccessful) {
            throw Exception(response.errorBody()?.string() ?: "")
        }
        val response1 = service(token_).getMRWithBase(projectId = "$owner_/$repo_", target = branch).execute()
        if (!response1.isSuccessful) {
            throw Exception(response1.errorBody()?.string() ?: "")
        }

        return BranchInfo(
                name = branch,
                dependantPullRequests = response1.body()!!.map {
                    PullRequestInfo(
                            number = it.iid,
                            merged = it.state == "merged",
                            closed = it.state == "closed")
                },
                pullRequests = response.body()!!.map {
                    PullRequestInfo(
                            number = it.iid,
                            merged = it.state == "merged",
                            closed = it.state == "closed")
                }
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

        val response = service(token_).deleteBranch(
                projectId = "$owner_/$repo_",
                branch = ref
        ).execute()
        if (!response.isSuccessful) {
            throw Exception(response.body()?.string() ?: "")
        }
    }

    override fun isConfigured() =
            try {
                retrieveToken()
                true
            } catch (e: Exception) {
                false
            }

    override fun getAllBranches(
            token: String?,
            owner: String?,
            repo: String?
    ): List<String> {
        val token_ = token ?: retrieveToken()
        val owner_ = owner ?: repository().owner
        val repo_ = repo ?: repository().name

        val response = service(token_).getBranches(projectId = "$owner_/$repo_").execute()
        if (!response.isSuccessful) {
            throw Exception(response.errorBody()?.string() ?: "")
        }
        return response.body()?.map { it.name } ?: emptyList()
    }

    private fun retrieveToken(): String {
        return KintaEnv.get(KintaEnv.Var.GITLAB_PERSONAL_TOKEN)
                ?: throw Exception("Please provide GITLAB_PERSONAL_TOKEN env or put it in your kinta.properties")
    }

    data class Repository(val owner: String, val name: String)

    fun repository(): Repository {
        val git = Git(Project.repository)
        val remoteConfigList = git.remoteList().call()
        val uri = remoteConfigList.filter { it.name == "origin" }.first().urIs[0]
        return repoDetails(uri)
    }

    private fun repoDetails(uriIsh: URIish): Repository {
        if (uriIsh.host != "gitlab.com") {
            throw Exception("this script only works with gitlab.com")
        }
        // https://github.com/owner/repo.git has a leading '/'
        // git@github.com:owner/repo.git has not
        val s = uriIsh.path.trim('/').split("/")
        val repoName = s[1].removeSuffix(".git")
        return Repository(s[0], repoName)
    }
}