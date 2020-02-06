package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.integration.googleplay.GooglePlayIntegration
import com.dailymotion.kinta.integration.googleplay.LocalMetadataHelper
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.long


object UpdatePlayStoreChangeLogs : CliktCommand(name = "updatePlayStoreChangeLogs", help = "Update changelogs for a specific version cpde") {

    private val versionCode by argument("--versionCode", help = "The release version code to update").long()

    override fun run() {

        check(versionCode > 0) {
            "Invalid version code : $versionCode"
        }

        val changeLogs = LocalMetadataHelper.getChangelog(versionCode)
        if (changeLogs.isNotEmpty()) {
            GooglePlayIntegration.uploadWhatsNew(
                    versionCode = versionCode,
                    whatsNewProvider = { language ->
                        changeLogs.find { it.language == language }?.description
                    }
            )
        }

    }
}