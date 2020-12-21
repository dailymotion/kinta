package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.helper.CommandUtil
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import java.io.File


object PlayStorePublish : CliktCommand(name = "publish", help = "Publish a version on the given track") {

    private val archiveFile by option("--archiveFile")

    private val versionName by option("--versionName")

    private val versionCodeParam by option("--versionCode").long()

    private val percent by option("--percent", help = "The user fraction receiving the update")
            .double()
            .default(100.0)
            .validate { it > 0 && it <= 100 }

    private val updatePriority by option("--updatePriority")
            .int()
            .validate { it in 0..5 }

    private val track by argument("--track", help = "The Play Store track").choice(
            mapOf(*GooglePlayIntegration.GooglePlayTrack.values().map {
                Pair(it.name, it)
            }.toTypedArray())
    )

    private val localMetadataHelper = LocalMetadataHelper.getDefault()

    override fun run() {

        val versionCode = if (archiveFile != null) {
            Logger.i("Uploading file...")
            GooglePlayIntegration.uploadDraft(
                    archiveFile = File(archiveFile),
                    track = track
            )
        } else {
            versionCodeParam
        }


        check(versionCode != null && versionCode > 0) {
            "Invalid version code : $versionCode"
        }

        if (CommandUtil.prompt(
                        message = "You are going to a publish a new release to the track $track with version $versionCode." +
                                " (percent = $percent}, updatePriority = ${updatePriority ?: "not set"}" +
                                " Are you sure you want to proceed?",
                        options = listOf("yes", "no")
                ) != "yes") {
            return
        }

        GooglePlayIntegration.createRelease(
                track = track,
                releaseName = versionName ?: versionCode.toString(),
                listVersionCodes = listOf(versionCode),
                percent = percent,
                updatePriority = updatePriority
        )

        val changeLogs = localMetadataHelper.getChangelog(versionCode)
        if(changeLogs.isNotEmpty()){
            Logger.i("Uploading changelogs for version $versionCode...")
            GooglePlayIntegration.uploadWhatsNew(
                    versionCode = versionCode,
                    whatsNewProvider = { language ->
                        changeLogs.find { it.language == language }?.description
                    })
        }

    }
}