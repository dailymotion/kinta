package com.dailymotion.kinta.integration.googleplay.internal

import com.dailymotion.kinta.integration.git.GitIntegration
import com.dailymotion.kinta.integration.googleplay.LocalMetadataHelper
import com.github.ajalt.clikt.core.CliktCommand


object GetPlayStoreMetadata : CliktCommand(name = "getPlayStoreMetadata", help = "Pull listings from the Google Play, Changelogs from releases") {

    override fun run() {
        //Delete local files
        LocalMetadataHelper.cleanMetaDatas()

        //Get Google Play listings
        val listings = GooglePlayIntegration.getListings()

        //Get Google Play listings
        val changeLogs = GooglePlayIntegration.getChangeLogs()

        //Update local resources
        LocalMetadataHelper.saveListings(listings)
        LocalMetadataHelper.saveChangeLogs(changeLogs)

        //Add to git
        GitIntegration.add(LocalMetadataHelper.ANDROID_METADATA_FOLDER.path)
    }
}