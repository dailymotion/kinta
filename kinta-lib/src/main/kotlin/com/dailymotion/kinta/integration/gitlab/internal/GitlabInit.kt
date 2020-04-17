package com.dailymotion.kinta.integration.gitlab.internal

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.helper.CommandUtil
import com.github.ajalt.clikt.core.CliktCommand


object GitlabInit : CliktCommand(name = "init", help = "Set up Gitlab to use it with Kinta") {

    override fun run() {
        val personalToken = CommandUtil.prompt(message = "Provide your Gitlab personal token :")
        if (personalToken?.isNotBlank() == true) {
            KintaEnv.put(KintaEnv.Var.GITLAB_PERSONAL_TOKEN, personalToken)
            println("GITLAB_PERSONAL_TOKEN has been set to your .kinta/.env.properties file")
        }
    }
}