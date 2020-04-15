package com.dailymotion.kinta.integration.zip

import com.dailymotion.kinta.integration.commandline.CommandLine
import java.io.File

object ZipIntegration {
    /**
     *
     * @param input the directory to zip
     * @param output the produced zip file
     * @param baseDir the base used to create the hierarchy inside the zip. This is usually input.parentFile to
     * have only one root entry and avoid clutter when unzipping.
     */
    fun zip(input: File, output: File, baseDir: File? = null) {
        val workingDir = baseDir ?: input.parentFile
        val args = listOf("zip", "-r", "-y", output.relativeTo(workingDir).path, input.relativeTo(workingDir).path)

        val exitCode = ProcessBuilder(*args.toTypedArray())
                .directory(workingDir)
                .start()
                .waitFor()
        check(exitCode == 0) {
            "Cannot zip: ${input.absoluteFile}"
        }
    }
}