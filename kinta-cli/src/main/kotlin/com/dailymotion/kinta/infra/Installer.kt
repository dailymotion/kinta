package com.dailymotion.kinta.infra

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.zip.ZipInputStream

object Installer {
    fun installLatestVersion() {
        Constants.stagingDir.deleteRecursively()
        Constants.stagingDir.mkdirs()

        val response = "https://dailymotion.github.io/kinta/kinta.zip".let {
            Request.Builder().url(it)
                    .get()
                    .build()
                    .let {
                        OkHttpClient().newCall(it).execute()
                    }
        }

        ZipInputStream(response.body()!!.byteStream()).use { zipStream ->
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

        check(Constants.stagingDir.listFiles()!!.size == 1) {
            "The distribution zip contained too many root files: ${Constants.stagingDir.listFiles().map { it.name }.joinToString("\n")}"
        }

        val dir = Constants.stagingDir.listFiles()!!.first()
        check(dir.isDirectory) {
            "${dir.absolutePath} should be a directory"
        }

        Constants.currentDir.deleteRecursively()
        dir.renameTo(Constants.currentDir)
        Constants.stagingDir.delete()
    }
}