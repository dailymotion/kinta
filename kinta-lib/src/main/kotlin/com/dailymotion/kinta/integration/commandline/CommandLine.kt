package com.dailymotion.kinta.integration.commandline

import com.dailymotion.kinta.Project
import com.dailymotion.kinta.Project.projectDir
import java.io.File

object CommandLine {
    /**
     * Execute the given process
     *
     * @param commandLine the commandline. It is expected that all arguments in commandline
     * do not contain spaces. If they do, use @[execute]
     */
    fun execute(workingDir: File = projectDir, commandLine: String) {
        execute(workingDir, *commandLine.split(" ").toTypedArray())
    }

    fun execute(workingDir: File = projectDir, vararg args: String) {
        ProcessBuilder(*args)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
    }
}