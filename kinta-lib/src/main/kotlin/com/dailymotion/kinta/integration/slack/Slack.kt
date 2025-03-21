package com.dailymotion.kinta.integration.slack

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object Slack {

    /**
     * Send a slack notification
     *
     * @param webhookUrl: the webhook url as in https://api.slack.com/messaging/webhooks
     * If null, it will default to the SLACK_WEBHOOK_URL environnment variable.
     * @param channel: the slack channel to the message to
     * @param text: the body of the message
     */
    fun sendNotification(
            channel: String,
            text: String,
            webhookUrl: String? = null,
            username: String = "Release Bot",
            iconEmoji: String = ":robot_face:") {

        val webhookUrl_ = webhookUrl ?: KintaEnv.getOrFail(KintaEnv.Var.SLACK_WEBHOOK_URL)

        val jsonObject = JsonObject(
                mapOf(
                        "text" to JsonPrimitive(text),
                        "channel" to JsonPrimitive(channel),
                        "icon_emoji" to JsonPrimitive(iconEmoji),
                        "username" to JsonPrimitive(username)
                )
        )

        val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        Logger.i("Sending slack notification")

        val request = Request.Builder()
                .url(webhookUrl_)
                .post(body)
                .build()

        val response = OkHttpClient.Builder()
                .build()
                .newCall(request)
                .execute()

        if (!response.isSuccessful) {
            Logger.e("cannot send notif ${response.code}:\n${response.body?.string()}")
        }
    }
}