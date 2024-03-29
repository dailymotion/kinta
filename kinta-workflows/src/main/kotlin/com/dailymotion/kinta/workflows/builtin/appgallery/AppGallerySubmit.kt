package com.dailymotion.kinta.workflows.builtin.appgallery

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.integration.appgallery.AppGalleryIntegration
import com.github.ajalt.clikt.core.CliktCommand


object AppGallerySubmit : CliktCommand(name = "submit", help = "Submit all changes for review") {

    override fun run() {
        Logger.i("Task ==> Releasing app...")
        AppGalleryIntegration.submit()
    }
}