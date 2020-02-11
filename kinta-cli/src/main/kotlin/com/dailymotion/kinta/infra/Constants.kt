package com.dailymotion.kinta.infra

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object Constants {
    val homeDir = File(System.getProperty("user.home"))
    val kintaDir = File(homeDir, ".kinta")

    val currentDir = File(kintaDir, "current")
    val stagingDir = File(kintaDir, "staging")

    val latestVersion by lazy {
        Request.Builder().url("https://api.bintray.com/packages/dailymotion/com.dailymotion.kinta/kinta-lib/versions/_latest")
                .get()
                .build()
                .let {
                    OkHttpClient().newCall(it).execute().body()!!.string()
                }.let {
                    Json.nonstrict.parseJson(it).jsonObject.getPrimitive("name").content
                }.let {
                    Version(it)
                }
    }
}