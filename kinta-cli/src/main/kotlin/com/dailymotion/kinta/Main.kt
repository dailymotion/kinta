package com.dailymotion.kinta

import com.dailymotion.kinta.command.Bootstrap
import com.dailymotion.kinta.command.Init
import com.dailymotion.kinta.command.Update
import com.dailymotion.kinta.integration.gradle.Gradle
import com.dailymotion.kinta.workflows.BuiltInWorkflows
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import java.net.URLClassLoader
import java.util.*
import kotlin.system.exitProcess


val kintaSrcCommands by lazy {
    // Update the project workflows if needed
    if (Gradle(File("kintaSrc")).executeTask("assemble") != 0) {
        throw Exception("Exception assembling kintaSrc.")
    }

    val jarFile = File("./kintaSrc/build/libs/kinta-workflows-custom.jar")
    val urlClassLoader = URLClassLoader(arrayOf(jarFile.toURI().toURL()))
    val loader = ServiceLoader.load(Workflows::class.java, urlClassLoader)

    try {
        loader.flatMap { it.all() }
    } catch (serviceError: ServiceConfigurationError) {
        throw serviceError
    }
}

val mainCommands = listOf(
        Init,
        Bootstrap,
        Update
)

fun main(args: Array<String>) {
    val allCommands = if (File("kintaSrc").exists()) {
        mainCommands + kintaSrcCommands
    } else {
        mainCommands + BuiltInWorkflows.all()
    }

    object : CliktCommand(
            name = "kinta",
            help = "mobile workflows automation. Read more at https://dailymotion.github.io/kinta/",
            invokeWithoutSubcommand = true,
            printHelpOnEmptyArgs = true
    ) {
        val version by option("--version", "-V").flag()

        override fun run() {
            if (version) {
                println(VERSION)
                exitProcess(0)
            }
        }
    }.subcommands(allCommands.sortedBy { it.commandName })
            .main(args)

    // The apollo threadpools are still busy so don't wait for them and just exit the program
    // See https://github.com/apollographql/apollo-android/issues/1896
    exitProcess(0)
}