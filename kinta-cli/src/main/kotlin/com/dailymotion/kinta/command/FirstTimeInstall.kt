package com.dailymotion.kinta.command

import com.dailymotion.kinta.infra.Constants.currentDir
import com.github.ajalt.clikt.core.CliktCommand
import java.io.File

object FirstTimeInstall : CliktCommand(name = "firstTimeInstall", help = "Create activation scripts for kinta." +
        " This is called by the install script and you shouldn't need to call it manually") {
    override fun run() {
        val initFile = File(currentDir, "setup-environment.sh")

        initFile.writeText("""
            export PATH=$currentDir/bin:${'$'}PATH
        """.trimIndent())

        val sourceCommand = """
            source ${initFile.absolutePath}
        """.trimIndent()

        println("""
            Kinta has been successfully installed.
            
            To get started, type:

            $sourceCommand
            
            Then type 'kinta' to get a list of commands.
        """.trimIndent())
    }
}