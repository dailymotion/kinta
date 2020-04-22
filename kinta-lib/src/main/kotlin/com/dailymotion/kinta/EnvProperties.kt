package com.dailymotion.kinta

import java.io.File
import java.util.*

object EnvProperties {
    const val filename = "env.properties"

    private val properties = Properties()
    private val file by lazy {
        val projectDir = Project.findBaseDir()

        if (projectDir != null) {
            File(projectDir, ".kinta/$filename")
        } else {
            File(".kinta/$filename")
        }
    }

    init {
        try {
            properties.load(file.inputStream())
        } catch (e: Exception) {
            // fail silently if we don't have any file yet
            //e.printStackTrace(System.err)
        }
    }

    fun put(key: String, value: String?) {
        if (value == null) {
            properties.remove(key)
        } else {
            properties.put(key, value)
        }
        properties.store(file.outputStream(), "Kinta Configuration file")
    }

    fun get(key: String): String? = properties.getProperty(key)

    /**
     * Update env.properties file to match all available envs
     * This method should be called during a kinta update for example
     */
    fun updateAvailableBuiltInEnvs(keys: List<String>) {
        if (file.exists()) {
            properties.store(file.outputStream(), "Kinta Configuration file")

            //Write envs comments
            val undefinedEnvs = keys.filter { !properties.containsKey(it) }
            val comments = undefinedEnvs.joinToString(separator = "\n", prefix = "\n") { "#${it}={${it}}" }
            file.writeText(file.readText() + comments)

            //Sync property file
            properties.load(file.inputStream())
        }
    }

}