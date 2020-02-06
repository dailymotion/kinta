package com.dailymotion.kinta.workflows.builtin.playstore

import com.dailymotion.kinta.integration.googleplay.GooglePlayIntegration
import com.dailymotion.kinta.integration.googleplay.LocalMetadataHelper
import com.github.ajalt.clikt.core.CliktCommand


object UpdatePlayStoreListings : CliktCommand(name = "updatePlayStoreListings", help = "Push listings to the Google Play") {

    override fun run() {
        /**
         * Get local listings
         */
        val localResources = LocalMetadataHelper.getAllListing()
        /**
         * Get Play Store listings
         */
        val playStoreResources = GooglePlayIntegration.getListings()
        /**
         * Elements added or updated locally
         */
        val updateList = localResources.filter { resource ->
            playStoreResources.find { it == resource } == null
        }
        /**
         * Elements removed locally
         */
        val removeList = playStoreResources.filter { resource ->
            localResources.find { it.language == resource.language } == null
        }

        if (updateList.isEmpty() && removeList.isEmpty()) {
            println("Nothing to update.")
            return
        }
        if (updateList.isNotEmpty()) {
            println("You're about to UPDATE languages : ${updateList.joinToString(separator = ", ") { it.language }}")
        }
        if (removeList.isNotEmpty()) {
            println("You're about to REMOVE languages : ${removeList.joinToString(separator = ", ") { it.language }}")
        }
        println("Are you sure you want to proceed? [yes/no]?")
        loop@ while (true) {
            when (readLine()) {
                "yes" -> break@loop
                "no" -> return
            }
        }

        if (updateList.isNotEmpty()) {
            GooglePlayIntegration.uploadListing(resources = updateList)
        }
        if (removeList.isNotEmpty()) {
            GooglePlayIntegration.removeListings(languagesList = removeList.map { it.language })
        }
        println("Done!")
    }
}