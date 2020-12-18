package com.dailymotion.kinta.workflows.builtin.appgallery

import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.helper.CommandUtil
import com.dailymotion.kinta.integration.appgallery.AppGalleryIntegration
import com.dailymotion.kinta.workflows.builtin.playstore.LocalMetadataHelper
import com.github.ajalt.clikt.core.CliktCommand


object AppGalleryUpdateListings : CliktCommand(name = "updateListings", help = "Push listings to AppGallery") {

    override fun run() {
        Logger.i("Task ==> Update App Gallery listings...")
        /**
         * Get local listings
         */
        val localResources = LocalMetadataHelper.getAllListing()
        /**
         * Get Play Store listings
         */
        val appGalleryListings = AppGalleryIntegration.getListings()
        /**
         * Elements added or updated locally
         */
        val updateList = localResources.filter { resource ->
            appGalleryListings.find { it == resource } == null
        }
        /**
         * Elements removed locally
         */
        val removeList = appGalleryListings.filter { resource ->
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

        if(CommandUtil.prompt(
                message = "Are you sure you want to proceed?",
                options = listOf("yes", "no")
        ) != "yes"){
            return
        }

        if (updateList.isNotEmpty()) {
            updateList.forEach {
                AppGalleryIntegration.uploadListing(listing = it)
            }
        }
        if (removeList.isNotEmpty()) {
            removeList.forEach {
                AppGalleryIntegration.deleteListing(lang = it.language)
            }
        }
        Logger.i("Update App Gallery listings done !")
    }
}