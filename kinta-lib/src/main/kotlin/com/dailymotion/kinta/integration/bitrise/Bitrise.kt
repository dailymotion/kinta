package com.dailymotion.kinta.integration.bitrise

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


object Bitrise {

    private const val BITRISE_API_URL = "https://api.bitrise.io/v0.1/apps"
    private val json = Json { ignoreUnknownKeys = true }
    fun triggerBuild(
            token: String? = null,
            repoName: String,
            workflowId: String,
            branch: String = "master") {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.Var.BITRISE_PERSONAL_TOKEN)

        val appSlug = findAppSlug(token_, repoName)

        val body = JsonObject(mapOf(
                "payload" to JsonObject(mapOf(
                        "hook_info" to JsonObject(mapOf(
                                "type" to JsonPrimitive("bitrise")
                        )),
                        "build_params" to JsonObject(mapOf(
                                "branch" to JsonPrimitive(branch),
                                "workflow_id" to JsonPrimitive(workflowId)
                        ))
                ))
        ))

        val bodyAsString = body.toString()
        val requestBody = bodyAsString.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
                .header("Authorization", "token $token_")
                .url("$BITRISE_API_URL/$appSlug/builds")
                .post(requestBody)
                .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("cannot trigger build: ${response.code}: ${response.body!!.string()}")
        } else {
            response.body?.string()?.let {
                val element = json.parseToJsonElement(it)

                val buildUrl = element.jsonObject["build_url"]?.jsonPrimitive?.content
                buildUrl?.let { Logger.i("Build triggered : $it") }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun findAppSlug(token: String, repoName: String): String {

        val request = Request.Builder()
                .header("Authorization", "token $token")
                .url(BITRISE_API_URL)
                .get()
                .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("cannot get app list: ${response.code}: ${response.body!!.string()}")
        } else {
            response.body?.string()?.let { data ->
                val list: AppsListResponse = json.decodeFromString(data)
                return list.data.find { it.repo_slug == repoName }?.slug
                        ?: throw Exception("app not found. Current repo is ${repoName}")
            }
        }
        throw Exception("error parsing app list: ${response.code}: ${response.body!!.string()}")
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getAvailableWorkflows(
            token: String? = null,
            repoName: String
    ): List<String> {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.Var.BITRISE_PERSONAL_TOKEN)

        val appSlug = findAppSlug(token_, repoName)
        val request = Request.Builder()
                .header("Authorization", "token $token_")
                .url("$BITRISE_API_URL/$appSlug/build-workflows")
                .get()
                .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("cannot get workflows list: ${response.code}: ${response.body!!.string()}")
        }

        response.body?.string()?.let { data ->
            val list: WorkflowsResponse = json.decodeFromString(data)
            return list.data
        }
        throw Exception("error parsing workflows list: ${response.code}: ${response.body!!.string()}")
    }

    @Serializable
    private data class WorkflowsResponse(val data: List<String>)

    @Serializable
    private data class AppsListResponse(val data: List<AppItem>)

    @Serializable
    private data class AppItem(val slug: String, val repo_slug: String)
}