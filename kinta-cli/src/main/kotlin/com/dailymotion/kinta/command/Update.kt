package com.dailymotion.kinta.command

import com.dailymotion.kinta.VERSION
import com.dailymotion.kinta.infra.Constants.latestVersion
import com.dailymotion.kinta.infra.Installer
import com.dailymotion.kinta.infra.Version
import com.github.ajalt.clikt.core.CliktCommand
import kotlin.system.exitProcess

object Update: CliktCommand(name = "update", help = "Update your kinta client") {
    override fun run() {
        val installedVersion = Version(VERSION)
        if (latestVersion <= installedVersion) {
            println("Version ${installedVersion.value} is the latest version")
            exitProcess(0)
        }

        Installer.installLatestVersion()
    }
}