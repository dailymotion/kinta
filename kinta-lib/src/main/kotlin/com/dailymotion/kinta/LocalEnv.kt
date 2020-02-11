package com.dailymotion.kinta

import java.io.File
import java.util.*

object LocalEnv {
    private val properties = Properties()
    private val file = File(Project.findBaseDir(), "local.env")

    init {
        try {
            properties.load(file.inputStream())
        } catch (e: Exception) {
            // fail silently if we don't have any file yet
            //e.printStackTrace(System.err)
        }
    }

    fun put(key: String, value: String?) {
        if (value ==null) {
            properties.remove(key)
        } else {
            properties.put(key, value)
        }
        properties.store(file.outputStream(), "Kinta Configuration file")
    }

    fun get(key: String): String? = properties.getProperty(key)
}