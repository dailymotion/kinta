package com.dailymotion.kinta.infra

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.Project
import com.dailymotion.kinta.integration.commandline.CommandLine
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

object Installer {
    private fun installUsingStagingDirectory() {
        Logger.i("Downloading version ${Constants.latestVersion.value}...")

        // We use curl and not okhttp here to have the same process than install.sh
        val process = ProcessBuilder("curl -L -# https://dailymotion.github.io/kinta/zip/latest.zip".split(" "))
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

        ZipInputStream(process.inputStream.buffered()).use { zipStream ->
            var entry = zipStream.nextEntry
            while (entry != null) {
                val f = File(Constants.stagingDir, entry.name)

                if (entry.isDirectory) {
                    f.mkdirs()
                } else {
                    f.outputStream().use { outputStream ->
                        zipStream.copyTo(outputStream)
                    }
                }
                entry = zipStream.nextEntry
            }
            zipStream.closeEntry()
        }

        if (process.waitFor() != 0) {
            exitProcess(1)
        }

        check(Constants.stagingDir.listFiles()!!.size == 1) {
            "The distribution zip contained too many root files: ${Constants.stagingDir.listFiles().map { it.name }.joinToString("\n")}"
        }

        val dir = Constants.stagingDir.listFiles()!!.first()
        check(dir.isDirectory) {
            "${dir.absolutePath} should be a directory"
        }

        Constants.currentDir.deleteRecursively()
        dir.renameTo(Constants.currentDir)

        // Zip has some extra entry attributes that can keep file permissions but it's easier
        // to set them here manually
        File(Constants.currentDir, "bin/kinta").setExecutable(true)
        File(Constants.currentDir, "bin/kinta.bat").setExecutable(true)
        Logger.i("Update successful")
    }

    fun installLatestVersion() {
        Constants.stagingDir.deleteRecursively()
        Constants.stagingDir.mkdirs()

        try {
            installUsingStagingDirectory()
        } finally {
            Constants.stagingDir.delete()
        }
    }
}