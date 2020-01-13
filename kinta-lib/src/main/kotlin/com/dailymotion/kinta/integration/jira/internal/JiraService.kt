package com.dailymotion.kinta.integration.jira.internal

import retrofit2.Call
import retrofit2.http.*

interface JiraService {
    @GET("issue/{issueId}")
    fun getIssue(@Path("issueId") issueId: String): Call<Issue>

    @GET("issue/{issueId}/transitions")
    fun getTransitions(@Path("issueId") issueId: String): Call<TransitionResult>

    @POST("issue/{issueId}/transitions")
    fun setTransition(@Path("issueId") issueId: String, @Body transitionBody: TransitionBody): Call<Void>

    @PUT("issue/{issueId}/assignee")
    fun assign(@Path("issueId") issueId: String, @Body assignBody: AssignBody): Call<Void>

    @POST("issue/{issueId}/comment")
    fun addComment(@Path("issueId") issueId: String, @Body commentBody: CommentBody): Call<Void>
}
