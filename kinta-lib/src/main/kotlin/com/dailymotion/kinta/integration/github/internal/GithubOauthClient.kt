package com.dailymotion.kinta.integration.github.internal

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.Logger
import fi.iki.elonen.NanoHTTPD
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.security.SecureRandom

object GithubOauthClient {
    private val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val rnd = SecureRandom()
    private val lock = Object()

    val clientId: String
            get() = KintaEnv.get(KintaEnv.Var.GITHUB_APP_CLIENT_ID) ?: ""

    val clientSecret: String
        get() = KintaEnv.get(KintaEnv.Var.GITHUB_APP_CLIENT_SECRET) ?: ""

    private fun randomString(len: Int): String {
        val sb = StringBuilder(len)
        for (i in 0 until len)
            sb.append(AB[rnd.nextInt(AB.length)])
        return sb.toString()
    }

    class GithubCredentials(val username: String, val token: String)

    val githubCredentials by lazy {
        var token = KintaEnv.get(KintaEnv.Var.GITHUB_TOKEN)
        var username = KintaEnv.get(KintaEnv.Var.GITHUB_USERNAME)

        if (token == null || username == null) {
            username = "user"
            token = acquireToken()
            KintaEnv.put(KintaEnv.Var.GITHUB_USERNAME, username!!)
            KintaEnv.put(KintaEnv.Var.GITHUB_TOKEN, token!!)
        }

        GithubCredentials(username!!, token!!)
    }

    private fun acquireToken(): String {
        val state = randomString(16)
        val url = "https://github.com/login/oauth/authorize?client_id=$clientId&scope=repo&state=$state"
        var token: String? = null

        Logger.i("acquiring oauth token")

        val server = object : NanoHTTPD(8941) {
            override fun serve(session: IHTTPSession): Response {
                if (session.parms["state"] != state) {
                    return newFixedLengthResponse("Something went wrong")
                }
                val code = session.parms["code"]

                val postUrl = "https://github.com/login/oauth/access_token?client_id=$clientId" +
                        "&client_secret=$clientSecret" +
                        "&state=$state" +
                        "&code=$code"

                val request = Request.Builder()
                        .post("".toRequestBody("text/plain".toMediaTypeOrNull()))
                        .url(postUrl)
                        .header("Accept", "application/json")
                        .build()


                val response: okhttp3.Response

                try {
                    response = OkHttpClient.Builder()
                            .build()
                            .newCall(request)
                            .execute()
                } catch (e: Exception) {
                    throw Exception("cannot exchange code")
                }

                if (!response.isSuccessful) {
                    throw Exception("cannot exchange code")
                }
                val json = Json { ignoreUnknownKeys = true }

                synchronized(lock) {
                    token = json.parseToJsonElement(response.body!!.string())
                        .jsonObject["access_token"]
                        ?.jsonPrimitive
                        ?.content
                    lock.notify()
                }

                return newFixedLengthResponse("yay, you've been authorized !")
            }
        }

        server.start()

        openBrowser(url)

        synchronized(lock) {
            while (token == null) {
                lock.wait(1000)
            }
        }

        // sorry guys, no better solution than sleep until the response is sent
        // see https://github.com/NanoHttpd/nanohttpd/issues/355
        Thread.sleep(2000)

        server.stop()

        return token!!
    }


    private fun openBrowser(url: String) {
        val candidates = arrayOf("xdg-open", "open")

        val found = candidates.filter({ isInPath(it) })

        if (found.isEmpty()) {
            throw Exception("cannot open browser for github oauth")
        }

        Runtime.getRuntime().exec(arrayOf(found[0], url))
    }

    private fun isInPath(cmd: String): Boolean {
        val pathList = System.getenv("PATH").split(":").map { it.trim() }

        pathList.forEach {
            if (File(it, cmd).exists()) {
                return true
            }
        }

        return false
    }

    val credentials by lazy {
        UsernamePasswordCredentialsProvider(githubCredentials.username, githubCredentials.token)
    }

    fun getToken(): String {
        var token = KintaEnv.get(KintaEnv.Var.GITHUB_TOKEN)
        if (token != null) {
            return token
        }

        token = acquireToken()
        KintaEnv.put(KintaEnv.Var.GITHUB_TOKEN, token)

        return token
    }
}
