package com.dailymotion.kinta.integration.googleplay

data class GooglePlayRelease(
        val versionCodes: MutableList<Long>,
        val name: String,
        val status: String
)