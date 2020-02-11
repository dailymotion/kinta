package com.dailymotion.kinta.integration.bitrise

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody


object Bitrise {

    private const val BITRISE_API_URL = "https://api.bitrise.io/v0.1/apps"

    fun triggerBuild(
            token: String? = null,
            repoName: String,
            workflowId: String) {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.BITRISE_PERSONAL_TOKEN)

        val appSlug = findAppSlug(token_, repoName)

        val body = JsonObject(mapOf(
                "payload" to JsonObject(mapOf(
                        "hook_info" to JsonObject(mapOf(
                                "type" to JsonPrimitive("bitrise")
                        )),
                        "build_params" to JsonObject(mapOf(
                                "branch" to JsonPrimitive("master"),
                                "workflow_id" to JsonPrimitive(workflowId)
                        ))
                ))
        ))

        val bodyAsString = Json.nonstrict.toJson(body).toString()
        val requestBody = RequestBody.create(MediaType.parse("application/json"), bodyAsString)

        val request = Request.Builder()
                .header("Authorization", "token $token_")
                .url("$BITRISE_API_URL/$appSlug/builds")
                .post(requestBody)
                .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("cannot trigger build: ${response.code()}: ${response.body()!!.string()}")
        } else {
            response.body()?.string()?.let {
                val element = Json.nonstrict.parseJson(it)

                val buildUrl = element.jsonObject["build_url"]
                buildUrl?.let { Logger.d("Build triggered : $it") }
            }
        }
    }

    private fun findAppSlug(token: String, repoName: String): String {

        val request = Request.Builder()
                .header("Authorization", "token $token")
                .url(BITRISE_API_URL)
                .get()
                .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("cannot get app list: ${response.code()}: ${response.body()!!.string()}")
        } else {
            response.body()?.string()?.let { data ->
                val list: AppsListResponse = Json.nonstrict.parse(AppsListResponse.serializer(), data)
                return list.data.find { it.repo_slug == repoName }?.slug
                        ?: throw Exception("app not found. Current repo is ${repoName}")
            }
        }
        throw Exception("error parsing app list: ${response.code()}: ${response.body()!!.string()}")
    }

    fun getAvailableWorkflows(
            token: String? = null,
            repoName: String
    ): List<String> {

        val token_ = token ?: KintaEnv.getOrFail(KintaEnv.BITRISE_PERSONAL_TOKEN)

        val appSlug = findAppSlug(token_, repoName)

        val request = Request.Builder()
                .header("Authorization", "token $token")
                .url("$BITRISE_API_URL/$appSlug/build-workflows")
                .get()
                .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("cannot get workflows list: ${response.code()}: ${response.body()!!.string()}")
        }

        response.body()?.string()?.let { data ->
            val list: WorkflowsResponse = Json.nonstrict.parse(WorkflowsResponse.serializer(), data)
            return list.data
        }
        throw Exception("error parsing workflows list: ${response.code()}: ${response.body()!!.string()}")
    }

    @Serializable
    private data class WorkflowsResponse(val data: List<String>)

    @Serializable
    private data class AppsListResponse(val data: List<AppItem>)

    @Serializable
    private data class AppItem(val slug: String, val repo_slug: String)
}