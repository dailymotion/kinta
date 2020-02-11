package com.dailymotion.kinta

import com.dailymotion.kinta.Logger.LEVEL_DEBUG
import com.dailymotion.kinta.Logger.LEVEL_ERROR
import com.dailymotion.kinta.Logger.LEVEL_INFO

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

    fun init(args: Array<String>) {
        val logType = LogType.values().find { it.options.find { args.contains(it) } != null } ?: LogType.Info
        level = logType.logLevel
    }
}

enum class LogType(val options: List<String>, val logLevel: Int) {
    Debug(listOf("-d", "--debug"), logLevel = LEVEL_DEBUG),
    Info(listOf("-i", "--info"), logLevel = LEVEL_INFO),
    Error(listOf("-e", "--error"), logLevel = LEVEL_ERROR)
}