package com.dailymotion.kinta.integration.zip

import com.dailymotion.kinta.integration.commandline.CommandLine
import java.io.File

object Zip {
    /**
     *
     * @param input the directory to zip
     * @param output the produced zip file
     * @param baseDir the base used to create the hierarchy inside the zip. This is usually input.parentFile to
     * have only one root entry and avoid clutter when unzipping.
     */
    private fun zip(input: File, output: File, baseDir: File? = null) {
        val workingDir = baseDir ?: input.parentFile
        CommandLine.execute(workingDir, "zip", "-r", "-y", output.absolutePath, input.absolutePath)
    }
}