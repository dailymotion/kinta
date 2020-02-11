package com.dailymotion.kinta

object Logger {
    const val LEVEL_DEBUG = 0
    const val LEVEL_INFO = 1
    const val LEVEL_ERROR = 2

    var level = LEVEL_INFO

    private fun doLog(message: String, messageLevel: Int) {
        if (messageLevel >= level) {
            println(message)
        }
    }
    fun d(message: String) {
        doLog(message, LEVEL_DEBUG)
    }

    fun i(message: String) {
        doLog(message, LEVEL_INFO)
    }

    fun e(message: String) {
        doLog(message, LEVEL_ERROR)
    }
}
