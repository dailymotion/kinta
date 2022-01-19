package com.dailymotion.kinta.integration.transifex

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.globalJson
import com.dailymotion.kinta.integration.transifex.internal.model.TransifexService
import com.dailymotion.kinta.integration.transifex.internal.model.TxPullTranslationsPayload
import com.dailymotion.kinta.integration.transifex.internal.model.TxPushResourcePayload
import com.google.gson.Gson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


object Transifex {

    /**
     * Push source for a given resource
     * @param resource, the resource slug associated to the file we want to update
     */
    fun pushSource(
        token: String? = null,
        org: String? = null,
        project: String? = null,
        resource: String,
        content: String
    ) {
        val org_ = org ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_ORG)
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val payload = TxPushResourcePayload.createSourcePayload(
            org = org_,
            project = project_,
            resource = resource,
            content = content
        )

        pushResources(token, payload)
    }

    /**
     * Push Translation with the given resource and language
     * @param resource, the resource slug associated to the file we want to update
     * @param lang, the language code
     */
    fun pushTranslation(
        token: String? = null,
        org: String? = null,
        project: String? = null,
        resource: String,
        lang: String,
        content: String
    ) {
        val org_ = org ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_ORG)
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val payload = TxPushResourcePayload.createTranslationPayload(
            org = org_,
            project = project_,
            resource = resource,
            content = content,
            lang = lang
        )

        pushResources(token, payload)
    }

    private fun pushResources(
        token: String? = null,
        payload: TxPushResourcePayload
    ) {
        /** The API is very strict related to the ContentType.
         * By setting a @Body and let the payload being converted, a "charset=utf-8" is
         * automatically added, and the API will return a 500 error.
         * By converting the data to byteArray, we avoid this behavior **/
        val dataByteArray = Gson().toJson(payload).toByteArray()
        val requestBody = RequestBody.create(MediaType.get("application/vnd.api+json"), dataByteArray)
        val response = service(token).requestUploadResource(payload.data.type, requestBody).execute()

        check (response.isSuccessful) {
            "Transifex: cannot push Resource: ${response.code()}: ${response.errorBody()?.string()}"
        }

        val uploadId = globalJson.parseToJsonElement(response.body()?.string().orEmpty())
            .jsonObject["data"]?.jsonObject
            ?.getValue("id")?.jsonPrimitive?.content!!

        checkUploadCompleted(token, payload.data.type, uploadId)
    }

    /**
     * Check an upload status
     * @param uploadId, the upload id associated
     * @param uploadType, the type of upload that has been done
     */
    private fun checkUploadCompleted(
        token: String?,
        uploadType: String,
        uploadId: String
    ) {
        /** Wait a bit before checking the upload status
         * to have a chance, it is OK for the very first shot **/
        Thread.sleep(2000)

        val response = service(token).getUploadResourceStatus(uploadType, uploadId).execute()
        val responseBody = response.body()?.string()

        check (response.isSuccessful && responseBody != null) {
            "Transifex: cannot check upload status: ${response.code()}: ${response.errorBody()?.string()}"
        }

        val status = try {
            globalJson.parseToJsonElement(responseBody)
                .jsonObject["data"]
                ?.jsonObject?.get("attributes")
                ?.jsonObject?.getValue("status")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }

        when (status) {
            "failed" -> throw IllegalStateException("Upload failed : $responseBody")
            "succeeded" -> {
                /** Upload was successful **/
                return
            }
            else -> {
                /** The upload has not been performed yet, come back later **/
                checkUploadCompleted(token, uploadType, uploadId)
            }
        }
    }

    /**
     * Get the languages used in the given project
     * @return the list of transifex language codes
     */
    fun getLanguages(
        token: String? = null,
        org: String? = null,
        project: String? = null
    ): List<String> {
        val org_ = org ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_ORG)
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val projectId = "o:$org_:p:$project_"
        val response = service(token).getLanguages(projectId).execute()

        check (response.isSuccessful) {
            "Transifex: cannot get languages: ${response.code()}: ${response.errorBody()?.string()}"
        }

        return response.body()!!.data.map { it.code }
    }

    /**
     * Get Translation content for a specific language and resource
     * @param resource, the resource slug we want to fetch
     * @param lang the language code associated to the content wanted
     * @return the content
     */
    fun getTranslation(
        token: String? = null,
        org: String? = null,
        project: String? = null,
        resource: String,
        lang: String,
        mode: String = "default"
    ): String {
        val org_ = org ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_ORG)
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_PROJECT)

        val payload = TxPullTranslationsPayload.with(
            org = org_,
            project = project_,
            resource = resource,
            lang = lang,
            mode = mode
        )
        /** The API is very strict related to the ContentType.
         * By setting a @Body and let the payload being converted, a "charset=utf-8" is
         * automatically added, and the API will return a 500 error.
         * By converting the data to byteArray, we avoid this behavior **/
        val dataByteArray = Gson().toJson(payload).toByteArray()
        val requestBody = RequestBody.create(MediaType.get("application/vnd.api+json"), dataByteArray)
        val response = service(token).requestDownloadTranslation(requestBody).execute()

        check (response.isSuccessful) {
            "Transifex: cannot request download for $lang: ${response.code()}: ${response.errorBody()?.string()}"
        }

        val downloadId = globalJson.parseToJsonElement(response.body()?.string().orEmpty())
            .jsonObject["data"]?.jsonObject
            ?.getValue("id")?.jsonPrimitive?.content!!

        return checkDownloadStatus(token, downloadId)
    }

    /**
     * Check a download status for a translation
     * @param downloadId, the download id associated
     * @return content when ready
     */
    private fun checkDownloadStatus(
        token: String?,
        downloadId: String
    ): String {
        /** Wait a bit before checking the upload status
         * to have a chance, it is OK for the very first shot **/
        Thread.sleep(2000)

        val response = service(token).getDownloadTranslationStatus(downloadId).execute()
        val responseBody = response.body()?.string()

        check (response.isSuccessful && responseBody != null) {
            "Transifex: cannot check download status: ${response.code()}: ${response.errorBody()?.string()}"
        }

        val status = try {
            globalJson.parseToJsonElement(responseBody)
                .jsonObject["data"]
                ?.jsonObject?.get("attributes")
                ?.jsonObject?.getValue("status")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }

        return when (status) {
            null -> {
                /** We have been redirected to the resource we wanted to download **/
                responseBody
            }
            "failed" -> throw IllegalStateException("Translated file could not be compiled : $responseBody")
            else -> {
                /** The download is not ready yet, come back later **/
                checkDownloadStatus(token, downloadId)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun service(
        token: String?
    ): TransifexService {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.Var.TRANSIFEX_TOKEN)
        val logging = HttpLoggingInterceptor { Logger.d(it) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token_")
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rest.api.transifex.com/")
            .client(okHttpClient)
            .addConverterFactory(globalJson.asConverterFactory(MediaType.get("application/json")))
            .build()

        return retrofit.create(TransifexService::class.java)
    }

    val MODE_ONLYTRANSLATED = "onlytranslated"
}