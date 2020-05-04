package com.dailymotion.kinta.integration.xcode

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.integration.commandline.CommandLine
import java.io.File

@Suppress("NAME_SHADOWING")
object Notarization {
    fun sendForNotarization(username: String? = null,
                            password: String? = null,
                            bundleId: String,
                            itcProvider: String,
                            file: File): String {
        val password = password ?: KintaEnv.get(KintaEnv.Var.APPLE_PASSWORD)
        val username = username ?: KintaEnv.get(KintaEnv.Var.APPLE_USERNAME)

        val command = "xcrun altool --notarize-app --verbose --primary-bundle-id $bundleId --username $username --password $password --file ${file.absolutePath} -itc_provider $itcProvider"

        val result = CommandLine.output(command = command)

        println("result: $result")

        return result.lines().mapNotNull {
            val regex = Regex("RequestUUID = (.*)")
            val match = regex.matchEntire(it)
            if (match != null) {
                val requestId = match.groupValues[1]
                System.out.println("Notarization Request: ${requestId}")
                requestId
            } else {
                null
            }
        }.first()
    }

    fun showNotarizationHistory(username: String? = null,
                                password: String? = null,
                                itcProvider: String) {
        val password_ = password ?: KintaEnv.get(KintaEnv.Var.APPLE_PASSWORD)
        val username_ = username ?: KintaEnv.get(KintaEnv.Var.APPLE_USERNAME)

        val command = "xcrun altool --notarization-history 0 -u $username_ --password $password_ -itc_provider $itcProvider"

        ProcessBuilder().command(command.split(" "))
                .inheritIO()
                .start()
                .waitFor()
    }

    fun waitForNotarization(username: String? = null,
                            password: String? = null,
                            requestId: String) {
        val password_ = password ?: KintaEnv.get(KintaEnv.Var.APPLE_PASSWORD)
        val username_ = username ?: KintaEnv.get(KintaEnv.Var.APPLE_USERNAME)

        while (true) {
            val command = "xcrun altool --notarization-info $requestId -u $username_ --password $password_"

            val process = ProcessBuilder().command(command.split(" "))
                    .start()

            val result = process.inputStream.reader().readText()

            check(process.waitFor() == 0) {
                "Cannot run '$command': ${process.errorStream.reader().readText()}"
            }

            result.lines().forEach {
                val regex = Regex("RequestUUID = (.*)")
                val match = regex.matchEntire(it)
                if (match != null) {
                    System.out.println("Notarization Request: ${match.groupValues[1]}")
                    return
                }
            }

            Thread.sleep(10)
            print(".")
            System.out.flush()
        }
    }
}