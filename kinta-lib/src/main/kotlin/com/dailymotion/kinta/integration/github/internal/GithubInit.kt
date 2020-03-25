package com.dailymotion.kinta.integration.github.internal

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.helper.CommandUtil
import com.github.ajalt.clikt.core.CliktCommand


object GithubInit : CliktCommand(name = "initGithub", help = "Set up Github to use it with Kinta") {

    override fun run() {
        val clientId = CommandUtil.prompt(message = "Provide your GitHub app client id :")
        if (clientId?.isNotBlank() == true) {
            KintaEnv.put(KintaEnv.Env.GITHUB_APP_CLIENT_ID, clientId)
            println("GITHUB_APP_CLIENT_ID has been set to your kinta.properties")

            val clientSecret = CommandUtil.prompt(message = "Provide your Gitlab app client secret :")
            if (clientSecret?.isNotBlank() == true) {
                KintaEnv.put(KintaEnv.Env.GITHUB_APP_CLIENT_SECRET, clientSecret)
                println("GITHUB_APP_CLIENT_SECRET has been set to your kinta.properties")
            }
        }
    }
}