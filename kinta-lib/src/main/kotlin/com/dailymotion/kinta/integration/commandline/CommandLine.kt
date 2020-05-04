package com.dailymotion.kinta.integration.commandline

import com.dailymotion.kinta.Project
import com.dailymotion.kinta.Project.projectDir
import java.io.File

object CommandLine {
    private fun parse(command: String): Array<String> {
        return command.split(" ").toTypedArray()
    }

    private fun unparse(vararg args: String): String {
        return args.joinToString(",")
    }

    /**
     * Execute the given process
     *
     * @param commandLine the commandline. The parser used to tokenize `commandLine` is very basic.
     * If your arguments contains spaces, use @[execute]
     */
    fun exitCode(workingDir: File = File("."), command: String): Int {
        return exitCode(workingDir, *parse(command))
    }

    fun exitCode(workingDir: File = File("."), vararg args: String): Int {
        return ProcessBuilder(*args)
                .directory(workingDir)
                .inheritIO()
                .start()
                .waitFor()
    }

    fun executeOrFail(workingDir: File = File("."), command: String) {
        executeOrFail(workingDir, *parse(command))
    }

    fun executeOrFail(workingDir: File = File("."), vararg args: String) {
        val exitCode = ProcessBuilder(*args)
                .directory(workingDir)
                .inheritIO()
                .start()
                .waitFor()
        check(exitCode == 0) {
            "Cannot execute ${unparse(*args)}: $exitCode"
        }
    }

    fun output(workingDir: File = File("."), command: String): String {
        return output(workingDir, *parse(command))
    }

    fun output(workingDir: File = File("."), vararg args: String): String {
        val process = ProcessBuilder(*args)
                .directory(workingDir)
                .redirectError(ProcessBuilder.Redirect.INHERIT) // by default the stderr is piped and may block
                .start()

        val exitCode = process.waitFor()
        check(exitCode == 0) {
            "Cannot execute ${unparse(*args)}: $exitCode"
        }

        return process.inputStream.reader().readText()
    }
}