package com.dailymotion.kinta.helper

object CommandUtil {

    fun prompt(message: String, options: List<String> = emptyList()): String? {
        message.split("/n").forEach {
            println(it)
        }
        if (options.isNotEmpty()) {
            println("[${options.joinToString("/")}] ?")
        }
        val result: String?
        loop@ while (true) {
            val line = readLine()
            if (options.contains(line) || options.isEmpty()) {
                result = line
                break@loop
            }
        }
        return result
    }

}