package com.dailymotion.kinta.integration.transifex.internal.model

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TransifexService {

    @POST("{download_type}")
    fun requestDownloadTranslation(@Path("download_type") type: String, @Body payload: RequestBody): Call<ResponseBody>

    @GET("{download_type}/{id}")
    fun getDownloadTranslationStatus(@Path("download_type") type: String, @Path("id") id: String): Call<ResponseBody>

    @POST("{upload_type}")
    fun requestUploadResource(@Path("upload_type") type: String, @Body payload: RequestBody): Call<ResponseBody>

    @GET("{upload_type}/{id}")
    fun getUploadResourceStatus(@Path("upload_type") type: String, @Path("id") id: String): Call<ResponseBody>

    @GET("projects/{projectSlug}/languages")
    fun getLanguages(@Path("projectSlug") projectSlug: String,): Call<TxSupportedLanguagesResponse>
}