package com.dailymotion.kinta.workflows.builtin.appgallery

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.helper.CommandUtil
import com.github.ajalt.clikt.core.CliktCommand

object AppGalleryInit : CliktCommand(
        name = "init",
        help = "Convenient workflow to set up App Gallery requirements in order to use other App Gallery workflows (upload, submit, ...)"
) {
    override fun run() {
        val packageName = CommandUtil.prompt(message = "Provide your app package name :")
        if (packageName?.isNotBlank() == true) {
            KintaEnv.put(KintaEnv.Var.APPGALLERY_PACKAGE_NAME, packageName)
            println("APPGALLERY_PACKAGE_NAME has been set to your .kinta/.env.properties file")
        }

        val clientId = CommandUtil.prompt(message = "Provide the App Gallery clientId :")
        if (clientId?.isNotBlank() == true) {
            KintaEnv.put(KintaEnv.Var.APPGALLERY_CLIENT_ID, clientId)
            println("APPGALLERY_CLIENT_ID has been set to your .kinta/.env.properties file")
        }

        val clientSecrets = CommandUtil.prompt(message = "Provide your App Gallery clientSecrets :")
        if (clientSecrets?.isNotBlank() == true) {
            KintaEnv.put(KintaEnv.Var.APPGALLERY_CLIENT_SECRET, clientSecrets)
            println("APPGALLERY_CLIENT_SECRET has been set to your .kinta/.env.properties file")
        }
    }
}