package com.dailymotion.kinta.integration.appcenter

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.helper.executeOrFail
import com.dailymotion.kinta.helper.newOkHttpClient
import kotlinx.serialization.json.*
import okhttp3.*
import java.io.File

@Suppress("NAME_SHADOWING")
object AppCenter {
    val json = Json {
        ignoreUnknownKeys = true
    }

    fun uploadApp(
            token: String? = null,
            organization: String? = null,
            appId: String,
            destinationName: String,
            file: File,
            changelog: String?) {

        val token = token ?: KintaEnv.getOrFail(KintaEnv.Var.APPCENTER_TOKEN)
        val organisation = organization ?: KintaEnv.getOrFail(KintaEnv.Var.APPCENTER_ORGANIZATION)

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token)
                .post(RequestBody.create(null, ByteArray(0)))
                .url("https://api.appcenter.ms/v0.1/apps/$organisation/$appId/release_uploads")
                .build()

        Logger.i("Getting AppCenter upload url...")
        val response = request.executeOrFail()

        val uploadUrlData = json.parseToJsonElement(response.string()).jsonObject

        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val builderUpload = MultipartBody.Builder()
                .setType(MediaType.parse("multipart/form-data")!!)
                .addFormDataPart("ipa", file.name, fileBody)
        val requestUpload = Request.Builder()
                .post(builderUpload.build())
                .url(uploadUrlData["upload_url"]?.jsonPrimitive?.content ?: error("cannot find upload_url"))
                .build()

        Logger.i("Uploading...")
        requestUpload.executeOrFail()

        Logger.i("Upload OK ! Committing...")
        val requestCommit = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token)
                .patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{ \"status\" : \"committed\" }"))
                .url("https://api.appcenter.ms/v0.1/apps/$organisation/$appId/release_uploads/${uploadUrlData["upload_id"]?.jsonPrimitive?.content}")
                .build()

        val commitBody = requestCommit.executeOrFail()

        val releaseData = json.parseToJsonElement(commitBody.string()).jsonObject

        Logger.i("Commited ! Updating release notes...")
        val postData = JsonObject(
                mapOf(
                        "destination_name" to JsonPrimitive(destinationName),
                        "release_notes" to JsonPrimitive(changelog)
                )
        )

        val requestReleaseNotes = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token)
                .patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postData.toString()))
                .url("https://api.appcenter.ms/${releaseData["release_url"]?.jsonPrimitive?.content}")
                .build()

        requestReleaseNotes.executeOrFail()
        Logger.i("Release notes updated")
    }

    fun uploadDsym(
            token: String? = null,
            organization: String? = null,
            appId: String,
            dsymFile: File
    ) {
        val token = token ?: KintaEnv.getOrFail(KintaEnv.Var.APPCENTER_TOKEN)
        val organisation = organization ?: KintaEnv.getOrFail(KintaEnv.Var.APPCENTER_ORGANIZATION)

        val postData  = JsonObject(mapOf(
                "symbol_type" to JsonPrimitive("Apple")
        ))
        val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                postData.toString()
        )

        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-API-Token", token)
                .post(requestBody)
                .url("https://api.appcenter.ms/v0.1/apps/$organisation/$appId/symbol_uploads")
                .build()

        val body = request.executeOrFail()

        val uploadUrlData = json.parseToJsonElement(body.string()).jsonObject

        val uploadRequest = Request.Builder()
                .url(uploadUrlData["upload_url"]?.jsonPrimitive?.content ?: error("no upload_url"))
                .put(RequestBody.create(MediaType.parse("application/octet-stream"), dsymFile))
                .header("x-ms-blob-type", "BlockBlob")
                .build()

        uploadRequest.executeOrFail()

        val postData2  = JsonObject(mapOf(
                "status" to JsonPrimitive("committed")
        ))
        val requestBody2 = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                postData2.toString()
        )

        val symbol_upload_id = uploadUrlData["symbol_upload_id"]?.jsonPrimitive?.content
        val url = "https://api.appcenter.ms/v0.1/apps/$organisation/$appId/symbol_uploads/$symbol_upload_id"

        val patchRequest = Request.Builder()
                .header("Accept", "application/json")
                .header("X-API-Token", token)
                .url(url)
                .patch(requestBody2)
                .build()

        patchRequest.executeOrFail()
    }
}