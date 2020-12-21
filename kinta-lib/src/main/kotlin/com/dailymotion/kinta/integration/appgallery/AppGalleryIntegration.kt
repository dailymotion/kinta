package com.dailymotion.kinta.integration.appgallery

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.helper.ProgressRequestBody
import com.dailymotion.kinta.integration.appgallery.internal.*
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.*
import okhttp3.*
import retrofit2.Retrofit
import java.io.File

object AppGalleryIntegration {

    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private const val API_URL = "https://connect-api.cloud.huawei.com/api/"

    private fun service(
            clientId: String? = null,
            token: String? = null
    ): AppGalleryService {

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor {
                    val builder = it.request().newBuilder()
                    token?.let { builder.header("Authorization", "Bearer $it") }
                    clientId?.let { builder.header("client_id", it) }
                    it.proceed(builder.build())
                }
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
                .build()

        return retrofit.create(AppGalleryService::class.java)
    }

    /**
     * Upload a new archive file
     */
    fun uploadDraft(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null,
            file: File
    ) {

        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)

        val appId = try {
            getAppId(clientId, clientSecret, packageName)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(clientId, clientSecret)
            getAppId(clientId, clientSecret, packageName)
        }
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        val fileName = file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1)
        val uploadData = getUploadUrl(clientId, token, appId, file.extension)
        val fileUrl = uploadFile(clientId, token, uploadData, fileName, file)
        updateAppFileInfo(clientId, token, appId, fileName, fileUrl)
    }

    /**
     * Submit the app for review
     */
    fun submit(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null
    ) {

        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)
        val appId = try {
            getAppId(clientId, clientSecret, packageName)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(clientId, clientSecret)
            getAppId(clientId, clientSecret, packageName)
        }
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        Logger.i("Submitting app to AppGallery...")
        val result = service(clientId, token).submit(appId, "1", RequestBody.create(JSON, JsonObject(mapOf()).toString())).execute()

        if(result.body()?.isSuccess() ?: false == false){
            val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
            throw IllegalStateException("Error submitting app to AppGallery : $error")
        }
    }

    /**
     * Upload a listing
     */
    fun uploadListing(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null,
            listing: GooglePlayIntegration.ListingResource
    ) {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)
        val appId = try {
            getAppId(clientId, clientSecret, packageName)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(clientId, clientSecret)
            getAppId(clientId, clientSecret, packageName)
        }
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        Logger.i("Uploading listing ${listing.language} to AppGallery...")
        val result = service(clientId, token).updateListings(appId, ListingBody(
                listing.language,
                listing.title ?: "",
                listing.description ?: "",
                listing.shortDescription ?: "")
        ).execute()

        if(result.body()?.isSuccess() ?: false == false){
            val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
            throw IllegalStateException("Error uploading listing $error")
        }
    }

    /**
     * Upload the changelog for a specific language
     */
    fun uploadChangelog(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null,
            lang: String,
            changelog: String
    ) {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)
        val appId = try {
            getAppId(clientId, clientSecret, packageName)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(clientId, clientSecret)
            getAppId(clientId, clientSecret, packageName)
        }
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        Logger.i("Uploading changelog for $lang to AppGallery...")
        val result = service(clientId, token).updateChangelog(appId, ChangelogBody(lang, changelog)).execute()

        if(result.body()?.isSuccess() ?: false == false){
            val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
            throw IllegalStateException("Error uploading changelog $error")
        }
    }

    /**
     * Delete a language listing
     */
    fun deleteListing(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null,
            lang: String
    ) {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)
        val appId = try {
            getAppId(clientId, clientSecret, packageName)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(clientId, clientSecret)
            getAppId(clientId, clientSecret, packageName)
        }
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)
        Logger.i("Deleting language $lang from AppGallery...")
        val result = service(clientId, token).deleteListing(appId, lang).execute()

        if(result.body()?.isSuccess() ?: false == false){
            val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
            throw IllegalStateException("Error deleting listing $error")
        }
    }

    private fun updateAppFileInfo(
            clientId: String,
            token: String,
            appId: String,
            fileName: String,
            fileUrl: String
    ) {

        val result = service(clientId, token).updateAppFileInfo(appId, com.dailymotion.kinta.integration.appgallery.internal.AppInfoFilesBody(
                fileType = "5",
                files = listOf(com.dailymotion.kinta.integration.appgallery.internal.AppInfoFilesBody.AppFileInfo(fileName, fileUrl))
        )).execute()

        val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
        throw IllegalStateException("Error updating app : $error")
    }

    private fun getAppId(
            clientId: String,
            clientSecret: String,
            packageName: String
    ): String {
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        Logger.i("Getting app id from AppGallery...")
        val result = service(clientId, token).getAppId(packageName).execute()
        result.body()?.appids?.let {
            return it[0].value
        }
        val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
        throw IllegalStateException("Can't retrieve app id for packageName $packageName, $error")
    }

    private fun getUploadUrl(
            clientId: String,
            token: String,
            appId: String,
            fileExtension: String
    ): UploadData {

        Logger.i("Getting upload url from AppGallery...")
        val result = service(clientId, token).getUploadUrl(appId, fileExtension).execute()

        val authCode = result.body()?.authCode
        val uploadUrl = result.body()?.uploadUrl

        if(authCode != null && uploadUrl != null){
            return UploadData(uploadUrl, authCode)
        }

        val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
        throw IllegalStateException("Error getting upload url : $error")
    }

    private fun uploadFile(
            clientId: String,
            token: String,
            uploadData: UploadData,
            fileName: String,
            file: File
    ): String {
        val mediaType = MediaType.parse("application/vnd.android.package-archive")

        Logger.i("Uploading file to AppGallery...")
        val result = service(clientId, token).upload(uploadData.uploadUrl,
                MultipartBody.Part.createFormData("file", fileName, ProgressRequestBody(RequestBody.create(mediaType, file), object : ProgressRequestBody.Listener {
                    override fun onProgress(progress: Int) {
                        Logger.d("Upload progress $progress")
                    }
                })),
                RequestBody.create(okhttp3.MultipartBody.FORM, fileName),
                RequestBody.create(okhttp3.MultipartBody.FORM, uploadData.authCode),
                RequestBody.create(okhttp3.MultipartBody.FORM, "1")
        ).execute()

        result.body()?.result?.uploadFileRsp?.fileInfoList?.let {
            return it[0].disposableURL
        }
        throw IllegalStateException("Error uploading to AppGallery")
    }

    fun getListings(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null
    ): List<GooglePlayIntegration.ListingResource> {

        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)
        val appId = try {
            getAppId(clientId, clientSecret, packageName)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(clientId, clientSecret)
            getAppId(clientId, clientSecret, packageName)
        }
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        Logger.i("Getting app info from AppGallery...")
        val result = service(clientId, token).getListings(appId).execute()

        result.body()?.languages?.let {
            return it.map{
                GooglePlayIntegration.ListingResource(
                        language = it.lang,
                        title = it.appName,
                        shortDescription = it.briefInfo,
                        description = it.appDesc,
                        video = null
                )
            }
        }
        val error = result.body()?.ret ?: result.errorBody()?.string() ?: result.code()
        throw IllegalStateException("Error retrieving app infos $error")
    }

    private fun acquireToken(
            clientId: String,
            clientSecret: String
    ): String {

        Logger.i("Getting token from AppGallery...")
        val result = service().acquireToken(TokenBody(
                grant_type = "client_credentials",
                client_id = clientId,
                client_secret = clientSecret
        ))

        result.execute().body()?.access_token?.let {
            KintaEnv.put(KintaEnv.Var.APPGALLERY_TOKEN, it)
            return it
        }
        throw IllegalStateException("Can't retrieve access token")
    }

    private fun getRequest(clientId: String, token: String) =
            Request.Builder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer $token")
                    .header("client_id", clientId)

    fun isConfigured() =
        KintaEnv.get(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)?.isNotBlank() == true
                && KintaEnv.get(KintaEnv.Var.APPGALLERY_CLIENT_ID)?.isNotBlank() == true
                && KintaEnv.get(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)?.isNotBlank() == true

}