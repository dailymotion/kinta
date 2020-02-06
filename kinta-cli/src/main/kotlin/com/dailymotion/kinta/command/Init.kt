package com.dailymotion.kinta.command

import com.dailymotion.kinta.integration.git.GitIntegration
import com.dailymotion.kinta.integration.gradle.Gradle
import com.github.ajalt.clikt.core.CliktCommand
import java.io.File

object Init : CliktCommand(name = "init", help = "Initializes kinta in a project. Run this at the root of your project.") {
    override fun run() {
        check(!File("kintaSrc").exists()) {
            "There is already a kintaSrc folder in this project. Did you mean to run `kinta update` instead?"
        }

        File("kintaSrc").mkdirs()
        copyResource("settings.gradle.kts", "kintaSrc/settings.gradle.kts")

        // Do this early to install the wrapper even if compilation fails
        // But after setting up settings.gradle.kts
        Gradle(File("kintaSrc")).executeTask("wrapper")

        File("kintaSrc/src/main/kotlin/com/dailymotion/kinta").mkdirs()
        File("kintaSrc/src/main/resources/META-INF/services").mkdirs()

        copyResource("CustomWorkflows.kt", "kintaSrc/src/main/kotlin/com/dailymotion/kinta/CustomWorkflows.kt")
        copyResource("com.dailymotion.kinta.Workflows", "kintaSrc/src/main/resources/META-INF/services/com.dailymotion.kinta.Workflows")
        copyResource("build.gradle.kts", "kintaSrc/build.gradle.kts")
        copyResource("gitignore", "kintaSrc/.gitignore")
        copyResource("kinta.properties", "kintaSrc/kinta.properties")

        //Assemble kintaSrc
        Gradle(File("kintaSrc")).executeTask("assemble")
        //Add kintaSrc to Git
        GitIntegration.add(File("kintaSrc").path)
    }

    private fun copyResource(resourceName: String, dirName: String) {
        javaClass.classLoader.getResourceAsStream(resourceName)!!.use { inputStream ->
            File(dirName).outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}