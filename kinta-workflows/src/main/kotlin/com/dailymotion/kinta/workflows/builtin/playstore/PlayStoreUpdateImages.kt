package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.helper.CommandUtil
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

/**
 * Push Play Store images for a specific imageType and a specific llanguage if provided
 */
object PlayStoreUpdateImages : CliktCommand(name = "updateImages", help = "Push metadata images to the Google Play") {

    private val imageTypeValue by option("-imageType", help = GooglePlayIntegration.ImageType.values().joinToString(separator = ", ") { it.value })
            .choice(*GooglePlayIntegration.ImageType.values().map { it.value }.toTypedArray())

    private val languageCode by option("-language", help = "the language code you want to update. Else all supported.")

    private val localMetadataHelper = LocalMetadataHelper.getDefault()

    override fun run() {
        /**
         * Retrieve local files
         */
        val listImages = localMetadataHelper.getImages(languageCode, imageTypeValue)

        if(CommandUtil.prompt(
                        message = "This will DELETE all images with type and language specified from PLAY STORE. Are you sure you want to proceed?",
                        options = listOf("yes", "no")) != "yes"){
            return
        }

        listImages.groupBy {
            Pair(it.languageCode, it.imageType)
        }.forEach { entry ->
            Logger.i("Updating ${entry.key.second} images for ${entry.key.first}")
            GooglePlayIntegration.uploadImages(
                    languageCode = entry.key.first,
                    imageType = entry.key.second,
                    images = entry.value.map { it.file },
                    overwrite = true
            )
        }
        Logger.i("Done!")
    }
}