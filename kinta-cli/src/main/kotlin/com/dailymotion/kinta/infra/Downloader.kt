package com.dailymotion.kinta.infra

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object Downloader {
    fun download(url: String, output: File) {
        Request.Builder().url(url)
                .get()
                .build()
                .let {
                    OkHttpClient().newCall(it).execute().body()!!.byteStream().use {inputStream ->
                        output.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
    }
}