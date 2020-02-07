package com.dailymotion.kinta.command

import com.dailymotion.kinta.infra.Constants.installedVersion
import com.dailymotion.kinta.infra.Constants.latestVersion
import com.dailymotion.kinta.infra.Installer
import com.github.ajalt.clikt.core.CliktCommand
import kotlin.system.exitProcess

object Update: CliktCommand(name = "update", help = "Update your kinta client") {
    override fun run() {
        if (installedVersion != null && latestVersion <= installedVersion) {
            println("Version ${installedVersion.value} is the latest version")
            exitProcess(0)
        }

        Installer.installLatestVersion()
    }
}