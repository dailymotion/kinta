package com.dailymotion.kinta.integration.commandline

import com.dailymotion.kinta.Project

object CommandLine {

    fun execute(commandLine: String) {
        ProcessBuilder(commandLine.split(" "))
                .directory(Project.projectDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
    }
}