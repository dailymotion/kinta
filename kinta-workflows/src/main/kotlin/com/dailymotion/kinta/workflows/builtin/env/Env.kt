package com.dailymotion.kinta.workflows.builtin.env

import com.dailymotion.kinta.EnvProperties
import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import java.util.*

private val set = object : CliktCommand(name = "set", help = "add a variable to your ${EnvProperties.filename} file") {
    val key by argument()
    val value by argument()

    override fun run() {
        KintaEnv.put(key, value)
        println("set $key=$value in ${EnvProperties.filename}")
    }
}

private fun definedVars(filename: String?): List<Pair<String, String>> {
    return if (filename != null) {
        Properties().apply {
            File(filename).bufferedReader().use {
                load(it)
            }
        }.entries
                .map { it.key as String to it.value as String}
    } else {
        KintaEnv.Var.values().mapNotNull {
            val value = KintaEnv.get(it)
            if (value != null) {
                it.name to value
            } else {
                null
            }
        }
    }
}

private val toShell = object : CliktCommand(name = "toShell", help = "export the current Kinta env to a shell script that you can source later on") {
    val output by argument()
    private val from by option(help = "use this properties file instead of the current environment")

    private fun String.escape(): String {
        // this is very rudimentary and should certainly be improved
        return this.replace(" ", "\\ ")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
    }

    override fun run() {
        definedVars(from).map {
            "${it.first}=${it.second.escape()}"
        }.joinToString(separator = "\n", postfix = "\n")
                .let {
                    File(output).writeText(it)
                }
    }
}

private val toProperties = object : CliktCommand(name = "toProperties", help = "export the current Kinta env to a properties file") {
    val output by argument()
    private val from by option(help = "use this properties file instead of the current environment")

    override fun run() {
        val properties = Properties()
        properties.putAll(definedVars(from))

        File(output).bufferedWriter().use {
            properties.store(it, "")
        }
    }
}

private val toGithub = object : CliktCommand(name = "toGithub", help = "export the current Kinta env to github secrets") {
    private val from by option(help = "use this properties file instead of the current environment")
    override fun run() {
        definedVars(from).forEach {
            GithubIntegration.setSecret(name = it.first, value = it.second)
        }
    }
}
val envCommand = object : CliktCommand(name = "env", help = "Set/export kinta environment variables") {
    override fun run() {

    }
}.subcommands(set,
        toShell,
        toProperties,
        toGithub
)

