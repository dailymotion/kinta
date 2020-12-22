package com.dailymotion.kinta.integration.appgallery.internal

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface AppGalleryService {
    @POST("oauth2/v1/token")
    fun acquireToken(@Body tokenBody: TokenBody): Call<TokenResult>

    @GET("publish/v2/appid-list")
    fun getAppId(@Query("packageName") packageName: String): Call<AppsIdsResult>

    @GET("publish/v2/upload-url")
    fun getUploadUrl(@Query("appId") appId: String, @Query("suffix") suffix: String): Call<UploadUrlResult>

    @Multipart
    @POST
    fun upload(@Url uploadUrl: String,
               @Part file: MultipartBody.Part,
               @Part("name") name: RequestBody,
               @Part("authCode") authCode: RequestBody,
               @Part("fileCount") fileCount: RequestBody): Call<UploadFileResult>

    @PUT("publish/v2/app-file-info")
    fun updateAppFileInfo(@Query("appId") appId: String, @Body appInfoFiles: AppInfoFilesBody): Call<CommonResult>

    @GET("publish/v2/app-info")
    fun getListings(@Query("appId") appId: String): Call<ListingResult>

    @PUT("publish/v2/app-language-info")
    fun updateListings(@Query("appId") appId: String, @Body listingBody: ListingBody): Call<CommonResult>

    @PUT("publish/v2/app-language-info")
    fun updateChangelog(@Query("appId") appId: String, @Body changelogBody: ChangelogBody): Call<CommonResult>

    @DELETE("publish/v2/app-language-info")
    fun deleteListing(@Query("appId") appId: String, @Query("lang") lang: String): Call<CommonResult>

    @POST("publish/v2/app-submit")
    fun submit(@Query("appId") appId: String, @Query("releaseType") releaseType: String, @Body requestBody: RequestBody): Call<CommonResult>
}
