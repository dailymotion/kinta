package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.long
import java.io.File


object PlayStorePublish : CliktCommand(name = "publish", help = "Publish a version on the given track") {

    private val archiveFile by option("--archiveFile")

    private val versionName by option("--versionName")

    private val versionCodeParam by option("--versionCode").long()

    private val percent by option("--percent", help = "The user fraction receiving the update").double()

    private val track by argument("--track", help = "The Play Store track").choice(
            mapOf(*GooglePlayIntegration.GooglePlayTrack.values().map {
                Pair(it.name, it)
            }.toTypedArray())
    )

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


        val percentToApply = percent ?: 100.0
        println("You are going to a publish a new release to the track $track with version $versionCode (percent = $percentToApply) Are you sure you want to proceed? [yes/no]?")
        loop@ while (true) {
            when (readLine()) {
                "yes" -> break@loop
                "no" -> return
            }
        }

        GooglePlayIntegration.createRelease(
                track = track,
                releaseName = versionName ?: versionCode.toString(),
                listVersionCodes = listOf(versionCode),
                percent = percentToApply
        )

        val changeLogs = LocalMetadataHelper.getChangelog(versionCode)
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