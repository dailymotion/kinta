package com.dailymotion.kinta.integration.jira

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.globalJson
import com.dailymotion.kinta.integration.jira.internal.AssignBody
import com.dailymotion.kinta.integration.jira.internal.CommentBody
import com.dailymotion.kinta.integration.jira.internal.Issue
import com.dailymotion.kinta.integration.jira.internal.JiraService
import com.dailymotion.kinta.integration.jira.internal.Transition
import com.dailymotion.kinta.integration.jira.internal.TransitionBody
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.util.Base64


object Jira {
    @OptIn(ExperimentalSerializationApi::class)
    private fun service(
        jiraUrl: String?,
        username: String?,
        password: String?
    ): JiraService {
        val jiraUrl_ = jiraUrl ?: KintaEnv.getOrFail(KintaEnv.Var.JIRA_URL)
        val username_ = username ?: KintaEnv.getOrFail(KintaEnv.Var.JIRA_USERNAME)
        val password_ = password ?: KintaEnv.getOrFail(KintaEnv.Var.JIRA_PASSWORD)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(username_, password_))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(jiraUrl_)
            .client(okHttpClient)
            .addConverterFactory(globalJson.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(JiraService::class.java)
    }

    fun moveToState(
        jiraUrl: String? = null,
        username: String? = null,
        password: String? = null,
        issueId: String,
        state: String
    ) {
        val service = service(jiraUrl, username, password)
        val response1 = service.getTransitions(issueId).execute()
        if (!response1.isSuccessful) {
            Logger.e("cannot get transistions for issue $issueId ==> ${response1.code()} / ${response1.errorBody()?.toString()}")
            return
        }

        val transitionResult = response1.body()!!

        Logger.d("Transitions available = ${transitionResult.transitions.joinToString(separator = ",") { it.to?.name.orEmpty() }}")
        val transition = transitionResult.transitions.firstOrNull { it.to?.name == state }

        if (transition == null) {
            Logger.e("cannot transition ticket $issueId :-/ ==> ${response1.code()} / ${response1.errorBody()?.toString()}")
            return
        }

        val response2 = service.setTransition(issueId, TransitionBody(Transition(transition.id))).execute()

        if (!response2.isSuccessful) {
            Logger.e("cannot change ticket $issueId status ==> ${response2.code()} / ${response2.errorBody()?.toString()}")
        }
    }

    fun assign(
        jiraUrl: String? = null,
        username: String? = null,
        password: String? = null,
        accountId: String? = null,
        issueId: String,
    ) {
        val accountId_ = accountId ?: KintaEnv.getOrFail(KintaEnv.Var.JIRA_ACCOUNT_ID)
        val response = service(jiraUrl, username, password).assign(issueId, AssignBody(accountId_)).execute()

        if (!response.isSuccessful) {
            throw Exception("Cannot assign jira issue $issueId to user with accountId $accountId_ : ${response.errorBody()?.string()}")
        }
    }

    fun getIssue(
        jiraUrl: String? = null,
        username: String? = null,
        password: String? = null,
        issueId: String
    ): Issue {
        val response = service(jiraUrl, username, password).getIssue(issueId).execute()

        if (!response.isSuccessful) {
            throw Exception("Cannot get jira issue $issueId: ${response.errorBody()?.string()}")
        }

        return response.body()!!
    }

    fun postComment(
        jiraUrl: String? = null,
        username: String? = null,
        password: String? = null,
        issueId: String,
        comment: String
    ) {
        val response = service(jiraUrl, username, password).addComment(issueId, CommentBody(comment)).execute()
        if (!response.isSuccessful) {
            Logger.e("cannot add comment to ticket $issueId status ==> ${response.code()} / ${response.errorBody()?.string()}")
        }
    }

    private class AuthInterceptor(val username: String, val password: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val builder = request.newBuilder()

            val s = String(Base64.getEncoder().encode("$username:$password".toByteArray()))
            builder.addHeader("Authorization", "Basic $s")

            return chain.proceed(builder.build())
        }
    }
}
