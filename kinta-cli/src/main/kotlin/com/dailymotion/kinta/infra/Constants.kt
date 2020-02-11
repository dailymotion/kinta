package com.dailymotion.kinta.infra

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object Constants {
    val homeDir = File(System.getProperty("user.home"))
    val kintaDir = File(homeDir, ".kinta")

    val currentDir = File(kintaDir, "current")
    val stagingDir = File(kintaDir, "staging")

    val latestVersion by lazy {
        Request.Builder().url("https://dailymotion.github.io/kinta/zip/latest.txt")
                .get()
                .build()
                .let {
                    OkHttpClient().newCall(it).execute().body()!!.string()
                }.let {
                    Version(it)
                }
    }
}