package com.dailymotion.console

import java.io.File

class Console {
    fun println(str: String) {
        kotlin.io.println(str)
    }

    /**
     * @param isInputValid return true if the input is valid, false else. By default all inputs are considered valid.
     */
    fun prompt(prompt: String): String {
        print(prompt)
        while (true) {
            val input = readLine()
            if (input != null) {
                return input
            }
        }
    }

    fun promptFile(prompt: String): File {
        while (true) {
            val path = prompt(prompt)
            val file =File(path)
            if (file.exists()) {
                return file
            }
            println("$path does not exist. Please check.")
        }
    }
}