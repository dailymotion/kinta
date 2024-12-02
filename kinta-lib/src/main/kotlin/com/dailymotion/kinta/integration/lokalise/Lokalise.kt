package com.dailymotion.kinta.integration.lokalise

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.globalJson
import com.dailymotion.kinta.helper.UnzipUtils
import com.dailymotion.kinta.integration.lokalise.internal.model.EmptyExport
import com.dailymotion.kinta.integration.lokalise.internal.model.LkDownloadPayload
import com.dailymotion.kinta.integration.lokalise.internal.model.LkLangResponse
import com.dailymotion.kinta.integration.lokalise.internal.model.LkUploadPayload
import com.dailymotion.kinta.integration.lokalise.internal.model.LokaliseService
import com.google.gson.Gson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.Closeable
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name


object Lokalise {

    private const val BASE_URL = "https://api.lokalise.com/api2/"
    private const val HEADER_TOKEN = "X-Api-Token"

    fun uploadResource(
        token: String? = null,
        project: String? = null,
        resource: String,
        lang: String,
        content: String,
        replaceModified: Boolean = true,
        cleanupMode: Boolean = true,
    ) {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.LOKALISE_PROJECT)

        val payload = LkUploadPayload(
            data = content,
            filename = resource,
            lang_iso = lang,
            replace_modified = replaceModified,
            cleanup_mode = cleanupMode,
        )

        requestUpload(
            token = token,
            projectId = project_,
            payload = payload
        )
    }

    fun downloadResource(
        token: String? = null,
        project: String? = null,
        resource: String,
        format: String,
        langList: List<String>,
        convertPlaceholders: Boolean = true,
        exportEmptyAs: String = EmptyExport.SKIP.name.lowercase(),
    ): LokaliseDownloadResponse {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.LOKALISE_PROJECT)

        val payload = LkDownloadPayload(
            filter_langs = langList,
            filter_filenames = listOf(resource),
            format = format,
            convert_placeholders = convertPlaceholders,
            export_empty_as = exportEmptyAs
        )

        return requestDownload(
            project = project_,
            token = token,
            payload = payload
        )
    }

    fun getLanguages(
        token: String? = null,
        project: String? = null
    ): List<String> {
        val project_ = project ?: KintaEnv.getOrFail(KintaEnv.Var.LOKALISE_PROJECT)

        val response = service(token).getLanguages(project_).execute()

        check (response.isSuccessful) {
            "Lokalise: cannot get languages: ${response.code()}: ${response.errorBody()?.string()}"
        }

        return response.body()!!.languages.map { it.lang_iso }
    }

    private fun requestUpload(
        token: String? = null,
        projectId: String,
        payload: LkUploadPayload,
    ) {
        val requestBody = Gson().toJson(payload).toRequestBody("application/json".toMediaType())
        val response = service(token).requestUpload(
            projectId = projectId,
            requestBody = requestBody,
        ).execute()

        check (response.isSuccessful) {
            "Lokalise: cannot push Resource: ${response.code()}: ${response.errorBody()?.string()}"
        }
    }

    private fun requestDownload(
        token: String? = null,
        project: String,
        payload: LkDownloadPayload,
    ): LokaliseDownloadResponse {

        val requestBody = Gson().toJson(payload).toRequestBody("application/json".toMediaType())
        val response = service(token).requestDownload(
            projectId = project,
            requestBody = requestBody
        ).execute()

        check (response.isSuccessful) {
            "Lokalise: cannot request download for ${payload.filter_langs}: ${response.code()}: ${response.errorBody()?.string()}"
        }

        val bundleUrl = globalJson.parseToJsonElement(response.body()?.string().orEmpty())
            .jsonObject.getValue("bundle_url").jsonPrimitive.content

        return extractDownloadResponse(bundleUrl)
    }

    private fun extractDownloadResponse(bundleUrl: String): LokaliseDownloadResponse {
        val url = URL(bundleUrl)
        val zipPath = Paths.get("lokalise.zip")
        val folderPath = Paths.get("lokalise")

        Logger.d("downloading archive...")

        url.openStream().use { Files.copy(it, zipPath) }

        Logger.d("unzipping archive...")

        UnzipUtils.unzip(
            zipFilePath = zipPath,
            destDirectory = folderPath
        )

        return LokaliseDownloadResponse(
            pathsToClean = listOf(zipPath,folderPath),
            response = folderPath.listDirectoryEntries().map {
                LkLangResponse(
                    lang_iso = it.name,
                    file = it.listDirectoryEntries().first().toFile()
                )
            }
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun service(
        token: String?
    ): LokaliseService {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.Var.LOKALISE_TOKEN)
        val logging = HttpLoggingInterceptor { Logger.d(it) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(HEADER_TOKEN, token_)
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(globalJson.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(LokaliseService::class.java)
    }
}

class LokaliseDownloadResponse(
    private val pathsToClean: List<Path>,
    val response: List<LkLangResponse>,
): Closeable {

    override fun close() {
        pathsToClean.forEach { it.toFile().deleteRecursively() }
    }
}
