package com.dailymotion.kinta.integration.appcenter

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

object AppCenter {
    fun uploadApp(
            token: String? = null,
            organization: String ?= null,
            appId: String,
            file: File,
            changelog: String?) {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.Env.APPCENTER_TOKEN)
        val organisation_ = organization ?: KintaEnv.getOrFail(KintaEnv.Env.APPCENTER_ORGANIZATION)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.NONE
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token_)
                .post(RequestBody.create(null, ByteArray(0)))
                .url("https://api.appcenter.ms/v0.1/apps/$organisation_/$appId/release_uploads")
                .build()

        Logger.i("Getting AppCenter upload url...")
        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            Logger.e("Cannot get AppCenter upload url")
            response.body()?.string()?.let { Logger.e(it) }
            return
        }

        val uploadUrlData = Json.nonstrict.parseJson(response.body()!!.string()).jsonObject

        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val builderUpload = MultipartBody.Builder()
                .setType(MediaType.parse("multipart/form-data")!!)
                .addFormDataPart("ipa", file.name, fileBody)
        val requestUpload = Request.Builder()
                .post(builderUpload.build())
                .url(uploadUrlData.getPrimitive("upload_url").content)
                .build()

        Logger.i("Uploading...")
        val responseUpload = okHttpClient.newCall(requestUpload).execute()
        if (!responseUpload.isSuccessful) {
            Logger.e("Cannot upload file to AppCenter")
            response.body()?.string()?.let { Logger.e(it) }
            return
        }

        Logger.i("Upload OK ! Committing...")
        val requestCommit = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token_)
                .patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{ \"status\" : \"committed\" }"))
                .url("https://api.appcenter.ms/v0.1/apps/$organisation_/$appId/release_uploads/${uploadUrlData.getPrimitive("upload_id").content}")
                .build()

        val responseCommit = okHttpClient.newCall(requestCommit).execute()
        if (!responseCommit.isSuccessful) {
            Logger.e("Cannot commit upload")
            responseCommit.body()?.string()?.let { Logger.e(it) }
            return
        }
        val releaseData = Json.nonstrict.parseJson(responseCommit.body()!!.string()).jsonObject

        Logger.i("Commited ! Updating release notes...")
        val postData = JsonObject(
                mapOf(
                        "destination_name" to JsonPrimitive("Everyone"),
                        "release_notes" to JsonPrimitive(changelog)
                )
        )

        val requestReleaseNotes = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token_)
                .patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Json.nonstrict.toJson(postData).toString()))
                .url("https://api.appcenter.ms/${releaseData.getPrimitive("release_url").content}")
                .build()

        val responseReleaseNotes = okHttpClient.newCall(requestReleaseNotes).execute()
        if (!responseReleaseNotes.isSuccessful) {
            Logger.e("Cannot update release notes")
            responseReleaseNotes.body()?.string()?.let { Logger.e(it) }
            return
        }
        Logger.i("Release notes updated")
    }
}