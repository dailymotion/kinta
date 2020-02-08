package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.github.ajalt.clikt.core.CliktCommand
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL


object GetPlayStorePreviews : CliktCommand(name = "pullPlayStorePreviews", help = "Pull images previews from the Google Play") {

    private const val PREVIEWS_FOLDER = "kintaSrc/playStorePreviews/"

    override fun run() {

        //Get Google Play images previews
        println("Getting images from Google Play. Please wait")
        val images = GooglePlayIntegration.getPreviews()

        //Create images tree
        images.groupBy { it.languageCode }.map {
            //Clean folder
            val imageFolder = File(PREVIEWS_FOLDER, it.key + "/images").apply {
                deleteRecursively()
                mkdirs()
            }

            it.value.forEach {
                val imageTypeFolder = File(imageFolder, it.imageType.value).apply {
                    mkdir()
                }
                saveImage(it.url, File(imageTypeFolder, it.id))
            }
        }
    }

    private fun saveImage(imageUrl: String, destinationFile: File) {
        lateinit var os: OutputStream
        lateinit var inputStream: InputStream
        try {
            val url = URL(imageUrl)
            inputStream = url.openStream()
            os = FileOutputStream(destinationFile)
            val b = ByteArray(2048)
            var length: Int
            while (inputStream.read(b).also { length = it } != -1) {
                os.write(b, 0, length)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream.close()
            os.close()
        }
    }
}