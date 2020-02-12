package com.dailymotion.kinta

import com.dailymotion.kinta.infra.FirstTimeInstall
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

enum class LogType(val options: List<String>, val logLevel: Int) {
    Debug(listOf("-d", "--debug"), logLevel = Logger.LEVEL_DEBUG),
    Info(listOf("-i", "--info"), logLevel = Logger.LEVEL_INFO),
    Error(listOf("-e", "--error"), logLevel = Logger.LEVEL_ERROR)
}

fun main(args: Array<String>) {
    Logger.level = LogType.values().find { it.options.find { args.contains(it) } != null }?.logLevel
            ?: Logger.LEVEL_INFO

    /*
     * A small hack to hide the firstTimeInstall from the help
     */
    val filteredArgs = if (args.contains("firstTimeInstall")) {
        FirstTimeInstall.run()
        args.filter { it != "firstTimeInstall" }.toTypedArray()
    } else {
        args
    }

    val additionalCommands = if (File("kintaSrc").exists()) {
        kintaSrcCommands
    } else {
        BuiltInWorkflows.all()
    }
    val allCommands = listOf(Init) + additionalCommands + Update 

    object : CliktCommand(
            name = "kinta",
            help = "mobile workflows automation. Read more at https://dailymotion.github.io/kinta/",
            invokeWithoutSubcommand = true,
            printHelpOnEmptyArgs = true
    ) {
        val version by option("--version", "-v").flag()
        val debug by option(*LogType.Debug.options.toTypedArray(), help = "set the logs to LOGLEVEL_DEBUG").flag()
        val info by option(*LogType.Info.options.toTypedArray(), help = "set the logs to LOGLEVEL_INFO (default)").flag()
        val error by option(*LogType.Error.options.toTypedArray(), help = "set the logs to LEVEL_ERROR").flag()

        override fun run() {
            if (version) {
                println(VERSION)
                exitProcess(0)
            }
        }
    }.subcommands(allCommands)
            .main(filteredArgs)

    // The apollo threadpools are still busy so don't wait for them and just exit the program
    // See https://github.com/apollographql/apollo-android/issues/1896
    exitProcess(0)
}