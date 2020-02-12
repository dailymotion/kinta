package com.dailymotion.kinta.integration.googleplay.internal

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.helper.CommandUtil
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonException
import java.io.File

object PlayStoreInit : CliktCommand(
        name = "init",
        help = "Convenient workflow to set up Play Store requirements in order to use other Play Store workflows (publish, listing, ...)"
) {
    override fun run() {
        // Recover the Play Json file
        val filePath = CommandUtil.prompt(message = "Please provide the Google Play Json file path :")

        if (filePath != null) {
            //Check at least the file is a json file
            try {
                val json = Json.nonstrict.parseJson(File(filePath).readText())
                KintaEnv.put(KintaEnv.GOOGLE_PLAY_JSON, json.toString())
                println("GOOGLE_PLAY_JSON has been set to your kinta.properties")
            } catch (e: JsonException) {
                println("Error while parsing the json file.")
                return
            }

            // Recover the app package name
            val packageName = CommandUtil.prompt(message = "Provide your app package name :")
            if (packageName?.isNotBlank() == true) {
                KintaEnv.put(KintaEnv.GOOGLE_PLAY_PACKAGE_NAME, packageName)
                println("GOOGLE_PLAY_PACKAGE_NAME has been set to your kinta.properties")

                // Ask for getting play store meta data
                if (CommandUtil.prompt(message = "Do you want to fetch the Play Store metadatas", options = listOf("yes", "no")) == "yes") {
                    PlayStorePullMetadatas.main(emptyList())
                }
            } else {
                println("App package name not provided. Exiting.")
            }
        } else {
            println("Google Play Json file not provided. Exiting.")
        }
    }
}