package com.dailymotion.kinta

import com.dailymotion.kinta.command.Init
import com.dailymotion.kinta.command.Run
import com.dailymotion.kinta.integration.gradle.Gradle
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import java.io.File
import kotlin.system.exitProcess


val subCommands: List<CliktCommand> by lazy {
    listOf(
            Run,
            Init
    )
}

val mainCommand: CliktCommand by lazy {
    object : CliktCommand(name = "kinta") {
        override fun run() {
        }
    }.subcommands(subCommands.sortedBy { it.commandName })
}

fun main(args: Array<String>) {
    val hasKintaDir = File("kintaSrc").exists()
    check(hasKintaDir || args.contains("init")) {
        "Kinta is not initialized. Please run 'kinta init' at the root of your project"
    }

    // Update the project workflows if needed
    if (hasKintaDir) {
        Gradle(File("kintaSrc")).executeTask("shadowJar")
    }
    mainCommand.main(args)

    // The apollo threadpools are still busy so don't wait for them and just exit the program
    exitProcess(0)
}