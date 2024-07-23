package com.dailymotion.kinta.integration.lokalise.internal.model

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LokaliseService {

    @POST("projects/{project_id}/files/download")
    fun requestDownload(
        @Path("project_id") projectId: String,
        @Body requestBody: RequestBody,
    ): Call<ResponseBody>

    @POST("projects/{project_id}/files/upload")
    fun requestUpload(
        @Path("project_id") projectId: String,
        @Body requestBody: RequestBody,
    ): Call<ResponseBody>

    @GET("projects/{project_id}/languages")
    fun getLanguages(@Path("project_id") projectId: String,): Call<LkSupportedLanguagesResponse>
}