package com.dailymotion.kinta.helper

import com.dailymotion.kinta.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Body

var okHttpLoggingLevel = HttpLoggingInterceptor.Level.NONE

fun newOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = okHttpLoggingLevel

    return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
}

fun Request.executeOrFail(): ResponseBody {
    val response = newOkHttpClient().newCall(this).execute()

    check(response.isSuccessful && response.body() != null) {
        val body = response.body()?.string()
        "Cannot execute ${this.url()}:\n$body"
    }

    return response.body()!!
}