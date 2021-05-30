package com.dailymotion.kinta.helper

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor

var okHttpLoggingLevel = HttpLoggingInterceptor.Level.NONE

fun newOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = okHttpLoggingLevel

    return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
}

fun Request.executeOrFail(
        errorMessage: String = "Cannot execute ${this.url()}",
        okHttpClient: OkHttpClient = newOkHttpClient()): ResponseBody {
    val response = okHttpClient.newCall(this).execute()
    if (response.isSuccessful && response.body() != null) {
        return response.body()!!
    } else {
        throw IllegalStateException("$errorMessage :\n${response.body()?.string()}")
    }
}