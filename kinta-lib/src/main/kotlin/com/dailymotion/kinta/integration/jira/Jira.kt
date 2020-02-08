package com.dailymotion.kinta.integration.jira

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Log
import com.dailymotion.kinta.integration.jira.internal.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.util.*


object Jira {
    private fun service(
            jiraUrl: String?,
            username: String?,
            password: String?
    ): JiraService {
        val jiraUrl_ = jiraUrl ?: KintaEnv.getOrFail(KintaEnv.JIRA_URL)
        val username_ = username ?: KintaEnv.getOrFail(KintaEnv.JIRA_USERNAME)
        val password_ = password ?: KintaEnv.getOrFail(KintaEnv.JIRA_PASSWORD)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(username_, password_))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(jiraUrl_)
                .client(okHttpClient)
                .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
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
            Log.e("cannot get transistions for issue $issueId ==> ${response1.code()} / ${response1.errorBody()?.toString()}")
            return
        }

        val transitionResult = response1.body()!!

        val transition = transitionResult.transitions.firstOrNull { it.to?.name == state }

        if (transition == null) {
            Log.e("cannot transition ticket $issueId :-/ ==> ${response1.code()} / ${response1.errorBody()?.toString()}")
            return
        }

        val response2 = service.setTransition(issueId, TransitionBody(Transition(transition.id))).execute()

        if (!response2.isSuccessful) {
            Log.e("cannot change ticket $issueId status ==> ${response2.code()} / ${response2.errorBody()?.toString()}")
        }
    }

    fun assign(
            jiraUrl: String? = null,
            username: String? = null,
            password: String? = null,
            issueId: String,
            jiraUserName: String) {
            val response = service(jiraUrl, username, password).assign(issueId, AssignBody(jiraUserName)).execute()

            if (!response.isSuccessful) {
                throw Exception("Cannot assign jira issue $issueId to $jiraUserName : ${response.errorBody()?.string()}")
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
            Log.e("cannot add comment to ticket $issueId status ==> ${response.code()} / ${response.errorBody()?.string()}")
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
