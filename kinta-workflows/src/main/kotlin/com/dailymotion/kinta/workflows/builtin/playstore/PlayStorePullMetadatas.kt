package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.integration.git.GitIntegration
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.github.ajalt.clikt.core.CliktCommand


object PlayStorePullMetadatas : CliktCommand(name = "pullMetadatas", help = "Pull listings from the Google Play, Changelogs from releases") {

    private val localMetadataHelper = LocalMetadataHelper.getDefault()

    override fun run() {
        //Delete local files
        localMetadataHelper.cleanMetaDatas()

        //Get Google Play listings
        val listings = GooglePlayIntegration.getListings()

        //Get Google Play listings
        val changeLogs = GooglePlayIntegration.getChangeLogs()

        //Update local resources
        localMetadataHelper.saveListings(listings)
        localMetadataHelper.saveChangeLogs(changeLogs)

        //Add to git
        GitIntegration.add(localMetadataHelper.ANDROID_METADATA_FOLDER.path)
    }
}