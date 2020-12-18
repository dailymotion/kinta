package com.dailymotion.kinta.integration.appgallery

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import kotlinx.serialization.json.*
import okhttp3.*
import java.io.File
import java.time.Duration

object AppGalleryIntegration {

    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private const val API_URL = "https://connect-api.cloud.huawei.com/api"

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
        updateAppFileInfo(clientId, token, appId, fileUrl, fileName)
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

        val data = JsonObject(mapOf())

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-submit")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .addQueryParameter("releaseType", "1")
                        .build()
                )
                .post(RequestBody.create(JSON, data.toString()))
                .build()

        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error releasing app to AppGallery : ${response.body()?.string()}"
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

        val data = JsonObject(
                mapOf(
                        "lang" to JsonPrimitive(listing.language),
                        "appName" to JsonPrimitive(listing.title),
                        "appDesc" to JsonPrimitive(listing.description),
                        "briefInfo" to JsonPrimitive(listing.shortDescription)
                )
        )

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-language-info")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .build()
                )
                .put(RequestBody.create(JSON, data.toString()))
                .build()

        Logger.i("Uploading listing ${listing.language} to AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error uploading listing : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        Logger.d(jsonResult.toString())
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

        val data = JsonObject(
                mapOf(
                        "lang" to JsonPrimitive(lang),
                        "newFeatures" to JsonPrimitive(changelog)
                )
        )

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-language-info")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .build()
                )
                .put(RequestBody.create(JSON, data.toString()))
                .build()

        Logger.i("Uploading changelog for $lang to AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error uploading changelog : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        Logger.d(jsonResult.toString())
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

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-language-info")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .addQueryParameter("lang", lang)
                        .build()
                )
                .delete()
                .build()

        Logger.i("Deleting language $lang from AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error deleting language : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        Logger.d(jsonResult.toString())
    }

    private fun updateAppFileInfo(
            clientId: String,
            token: String,
            appId: String,
            fileName: String,
            fileUrl: String
    ) {

        val files = JsonArray(
                listOf(
                        JsonObject(
                                mapOf(
                                        "fileName" to JsonPrimitive(fileName),
                                        "fileDestUrl" to JsonPrimitive(fileUrl)
                                )
                        )
                )
        )
        val data = JsonObject(mapOf(
                "fileType" to JsonPrimitive(5),
                "files" to files
        ))

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-file-info")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .build()
                )
                .put(RequestBody.create(JSON, data.toString()))
                .build()

        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error updating app : ${response.body()?.string()}"
        }
        Logger.d(response.body()?.string() ?: "")
    }

    private fun getAppId(
            clientId: String,
            clientSecret: String,
            packageName: String
    ): String {
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/appid-list")!!.newBuilder()
                        .addQueryParameter("packageName", packageName)
                        .build()
                )
                .build()

        Logger.i("Getting app id from AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error retrieving AppGallery app id : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        return jsonResult["appids"]!!.jsonArray[0].jsonObject["value"]?.content!!
    }

    private fun getUploadUrl(
            clientId: String,
            token: String,
            appId: String,
            fileExtension: String
    ): UploadData {

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/upload-url")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .addQueryParameter("suffix", fileExtension)
                        .build()
                )
                .build()

        Logger.i("Getting upload url from AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error getting upload url : ${response.code()} ${response.message()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        return UploadData(jsonResult["uploadUrl"]!!.content, jsonResult["authCode"]!!.content)
    }

    private fun uploadFile(
            clientId: String,
            token: String,
            uploadData: UploadData,
            fileName: String,
            file: File
    ): String {

        val okHttp = OkHttpClient.Builder()
                .callTimeout(Duration.ZERO)
                .writeTimeout(Duration.ZERO)
                .readTimeout(Duration.ZERO)
                .build()

        val mediaType = MediaType.parse("application/vnd.android.package-archive")
        val request = getRequest(clientId, token)
                .url(uploadData.uploadUrl)
                .post(MultipartBody.Builder()
                        .addFormDataPart("name", fileName)
                        .addFormDataPart("authCode", uploadData.authCode)
                        .addFormDataPart("fileCount", 1.toString())
                        .addFormDataPart("file", fileName, ProgressRequestBody(RequestBody.create(mediaType, file), object : ProgressRequestBody.Listener {
                            override fun onProgress(progress: Int) {
                                Logger.d("Upload progress $progress")
                            }
                        }))
                        .build())
                .build()

        Logger.i("Uploading file to AppGallery...")
        val response = okHttp.newCall(request).execute()
        check(response.isSuccessful) {
            "Error uploading to AppGallery : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        Logger.d(jsonResult.toString())
        val filesArray = jsonResult["result"]!!.jsonObject["UploadFileRsp"]!!.jsonObject.getArray("fileInfoList")
        return filesArray[0].jsonObject["disposableURL"]!!.content
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

        val request = getRequest(clientId, token)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-info")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .build()
                )
                .build()

        Logger.i("Getting app info from AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        check(response.isSuccessful) {
            "Error retrieving app infos : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        Logger.d(jsonResult.toString())
        return jsonResult.getArray("languages").map {
            GooglePlayIntegration.ListingResource(
                    language = it.jsonObject.getPrimitiveOrNull("lang").toString(),
                    title = it.jsonObject.getPrimitiveOrNull("appName").toString(),
                    shortDescription = it.jsonObject.getPrimitiveOrNull("briefInfo").toString(),
                    description = it.jsonObject.getPrimitiveOrNull("appDesc").toString(),
                    video = null
            )
        }
    }

    private fun acquireToken(
            clientId: String,
            clientSecret: String
    ): String {

        val data = JsonObject(mapOf(
                "grant_type" to JsonPrimitive("client_credentials"),
                "client_id" to JsonPrimitive(clientId),
                "client_secret" to JsonPrimitive(clientSecret)
        ))

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(JSON, data.toString()))
                .url("https://connect-api.cloud.huawei.com/api/oauth2/v1/token")
                .build()

        Logger.i("Getting token from AppGallery...")
        val response = OkHttpClient.Builder().build().newCall(request).execute()

        check(response.isSuccessful) {
            "Error retrieving AppGallery token : ${response.body()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        val token = jsonResult["access_token"]!!.content
        KintaEnv.put(KintaEnv.Var.APPGALLERY_TOKEN, token)
        return token
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