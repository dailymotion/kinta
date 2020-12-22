package com.dailymotion.kinta.workflows.builtin.appgallery

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.integration.appgallery.AppGalleryIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File


object AppGalleryUpload : CliktCommand(name = "upload", help = "Publish a version") {

    private val archiveFile by option("--archiveFile").required()

    override fun run() {
        Logger.i("Task ==> Upload file...")
        AppGalleryIntegration.uploadDraft(file = File(archiveFile))
    }
}