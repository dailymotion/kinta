package com.dailymotion.kinta.integration.gitlab.internal

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GitlabService {
    @POST("projects/{projectId}/merge_requests")
    fun openPullRequest(@Path("projectId") projectId: String, @Body mergeRequestBody: MergeRequestBody): Call<ResponseBody>

    @GET("projects/{projectId}/merge_requests")
    fun getMRWithHead(@Path("projectId") projectId: String, @Query("source_branch") source: String): Call<List<MergeRequest>>

    @GET("projects/{projectId}/merge_requests")
    fun getMRWithBase(@Path("projectId") projectId: String, @Query("target_branch") target: String): Call<List<MergeRequest>>

    @GET("projects/{projectId}/repository/branches")
    fun getBranches(@Path("projectId") projectId: String): Call<List<Branch>>

    @DELETE("projects/{projectId}/repository/branches/{branch}")
    fun deleteBranch(@Path("projectId") projectId: String, @Path("branch") branch: String): Call<ResponseBody>

    @PUT("projects/{projectId}/merge_requests/{mergeRequestId}")
    fun updatePullRequest(@Path("projectId") projectId: String, @Path("mergeRequestId") mergeRequestId: String, @Body mergeRequestBody: UpdateMergeRequestBody): Call<ResponseBody>

}
