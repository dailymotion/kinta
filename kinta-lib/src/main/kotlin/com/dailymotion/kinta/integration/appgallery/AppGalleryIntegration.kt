package com.dailymotion.kinta.integration.appgallery

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.content
import okhttp3.*
import java.io.File

object AppGalleryIntegration {

    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private const val API_URL = "https://connect-api.cloud.huawei.com/api"

    fun uploadDraft(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null,
            file: File
    ) {
        val appId = try {
            getAppId(client_id, client_secret, package_name)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(client_id, client_secret)
            getAppId(client_id, client_secret, package_name)
        }

        val uploadData = getUploadUrl(client_id, client_secret, package_name, appId, file.extension)
        uploadFile(client_id, client_secret, uploadData, file)
    }

    fun releaseApp(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null
    ) {
        val appId = try {
            getAppId(client_id, client_secret, package_name)
        } catch (e: Exception) {
            /** We probably have an invalid token **/
            acquireToken(client_id, client_secret)
            getAppId(client_id, client_secret, package_name)
        }

        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        val okHttp = OkHttpClient.Builder().build()
        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $token")
                .header("client_id", clientId)
                .url(HttpUrl.parse("$API_URL/publish/v2/app-submit")!!.newBuilder()
                        .addQueryParameter("appId", appId)
                        .build()
                )
                .post(RequestBody.create(null, byteArrayOf(0)))
                .build()

        val response = okHttp.newCall(request).execute()
        check(response.isSuccessful) {
            "Error uploading to AppGallery : ${response.body()?.string()}"
        }

    }

    private fun getAppId(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null
    ): String {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)

        val okHttp = OkHttpClient.Builder().build()

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $token")
                .header("client_id", clientId)
                .url(HttpUrl.parse("$API_URL/publish/v2/appid-list")!!.newBuilder()
                        .addQueryParameter("packageName", packageName)
                        .build()
                )
                .build()

        Logger.i("Getting app id from AppGallery...")
        val response = okHttp.newCall(request).execute()
        check(response.isSuccessful) {
            "Error retrieving AppGallery app id : ${response.body()?.string()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        return jsonResult["appids"]!!.jsonArray[0].jsonObject["value"]?.content!!
    }

    private fun getUploadUrl(
            client_id: String? = null,
            client_secret: String? = null,
            package_name: String? = null,
            appId: String,
            fileExtension: String
    ): UploadData {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)
        val packageName = package_name ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_PACKAGE_NAME)

        val okHttp = okhttp3.OkHttpClient.Builder().build()

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $token")
                .header("client_id", clientId)
                .url(HttpUrl.parse("$API_URL/publish/v2/upload-url")!!.newBuilder()
                        .addQueryParameter("packageName", packageName)
                        .addQueryParameter("appId", appId)
                        .addQueryParameter("suffix", fileExtension)
                        .build()
                )
                .build()

        Logger.i("Getting upload url from AppGallery...")
        val response = okHttp.newCall(request).execute()
        check(response.isSuccessful) {
            "Error getting upload url : ${response.code()} ${response.message()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        return UploadData(jsonResult["uploadUrl"]!!.content, jsonResult["authCode"]!!.content)
    }

    private fun uploadFile(
            client_id: String? = null,
            client_secret: String? = null,
            uploadData: UploadData,
            file: File
    ) {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)
        val token = KintaEnv.get(KintaEnv.Var.APPGALLERY_TOKEN) ?: acquireToken(clientId, clientSecret)

        val okHttp = OkHttpClient.Builder().build()

        val fileName = file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1)
        val mediaType = MediaType.parse("application/vnd.android.package-archive")
        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $token")
                .header("client_id", clientId)
                .url(uploadData.uploadUrl)
                .post(MultipartBody.Builder()
                        .addFormDataPart("name", fileName)
                        .addFormDataPart("authCode", uploadData.authCode)
                        .addFormDataPart("fileCount", 1.toString())
                        .addFormDataPart("file", fileName, RequestBody.create(mediaType, file))
                        .build())
                .build()

        val response = okHttp.newCall(request).execute()
        check(response.isSuccessful) {
            "Error uploading to AppGallery : ${response.body()?.string()}"
        }
    }

    private fun acquireToken(
            client_id: String? = null,
            client_secret: String? = null
    ): String {
        val clientId = client_id ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_ID)
        val clientSecret = client_secret
                ?: KintaEnv.getOrFail(KintaEnv.Var.APPGALLERY_CLIENT_SECRET)

        val data = JsonObject(mapOf(
                "grant_type" to JsonPrimitive("client_credentials"),
                "client_id" to JsonPrimitive(clientId),
                "client_secret" to JsonPrimitive(clientSecret)
        ))

        val okHttp = okhttp3.OkHttpClient.Builder().build()

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(JSON, data.toString()))
                .url("https://connect-api.cloud.huawei.com/api/oauth2/v1/token")
                .build()

        Logger.i("Getting token from AppGallery...")
        val response = okHttp.newCall(request).execute()

        check(response.isSuccessful) {
            "Error retrieving AppGallery token : ${response.body()}"
        }
        val jsonResult = Json.nonstrict.parseJson(response.body()?.string().orEmpty()).jsonObject
        val token = jsonResult["access_token"]!!.content
        KintaEnv.put(KintaEnv.Var.APPGALLERY_TOKEN, token)
        return token
    }
}