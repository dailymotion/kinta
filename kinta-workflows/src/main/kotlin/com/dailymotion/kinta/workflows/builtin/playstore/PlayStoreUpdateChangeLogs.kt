package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.long


object PlayStoreUpdateChangeLogs : CliktCommand(name = "updateChangeLogs", help = "Update changelogs for a specific version cpde") {

    private val versionCode by argument("--versionCode", help = "The release version code to update").long()

    private val localMetadataHelper = LocalMetadataHelper.getDefault()

    override fun run() {

        check(versionCode > 0) {
            "Invalid version code : $versionCode"
        }

        val changeLogs = localMetadataHelper.getChangelog(versionCode)
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