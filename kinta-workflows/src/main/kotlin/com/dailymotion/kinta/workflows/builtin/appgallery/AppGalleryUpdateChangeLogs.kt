package com.dailymotion.kinta.workflows.builtin.appgallery

import com.dailymotion.kinta.integration.appgallery.AppGalleryIntegration
import com.dailymotion.kinta.workflows.builtin.playstore.LocalMetadataHelper
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.long


object AppGalleryUpdateChangeLogs : CliktCommand(name = "updateChangeLogs", help = "Update changelogs for the current version") {

    private val versionCode by argument("--versionCode", help = "The version code changelog to upload").long()

    private val localMetadataHelper = LocalMetadataHelper.getDefault()

    override fun run() {

        check(versionCode > 0) {
            "Invalid version code : $versionCode"
        }

        val changeLogs = localMetadataHelper.getChangelog(versionCode)
        if (changeLogs.isNotEmpty()) {
            changeLogs.forEach {
                AppGalleryIntegration.uploadChangelog(
                        lang = it.language,
                        changelog = it.description
                )
            }
        }

    }
}