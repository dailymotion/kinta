package com.dailymotion.kinta.command

import com.dailymotion.kinta.helper.CommandUtil
import com.dailymotion.kinta.integration.googleplay.internal.PlayStoreInit
import com.github.ajalt.clikt.core.CliktCommand
import java.io.File

object Init : CliktCommand(name = "init", help = "Initialize a project.") {
    override fun run() {
        File(".kinta").mkdir()

        //Ask for the PLAY API service account
        if (CommandUtil.prompt(
                        message = "Would you like to set up the Play Store config (Useful for GooglePlayIntegration) ?\n" +
                                "Please refer to https://developers.google.com/android-publisher/getting_started and use a service_account.",
                        options = listOf("yes", "no")) == "yes") {

            PlayStoreInit.main(emptyList())
        }

        if (CommandUtil.prompt(
                        message = "Would you develop your own workflows ? (We will then create a kintaSrc build at the root of your project)\n" +
                                "You can do it later running again this command",
                        options = listOf("yes", "no")) == "yes") {

            InitCustomWorkflows.main(emptyList())
        }
    }
}