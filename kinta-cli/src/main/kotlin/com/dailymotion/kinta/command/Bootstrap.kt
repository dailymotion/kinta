package com.dailymotion.kinta.command

import com.dailymotion.kinta.infra.Constants.currentDir
import com.dailymotion.kinta.infra.Constants.homeDir
import com.dailymotion.kinta.infra.Installer
import com.github.ajalt.clikt.core.CliktCommand
import java.io.File

object Bootstrap : CliktCommand(name = "bootstrap", help = "Bootstrap a kinta installation in your home directory." +
        " This is called by the install script and you shouldn't need to call it manually") {
    override fun run() {
        val initFile = File(currentDir, "kinta-init.sh")

        initFile.writeText("""
            export PATH=$currentDir/bin:${'$'}PATH
        """.trimIndent())

        val sourceCommand = """
            source ${initFile.absolutePath}
        """.trimIndent()
        val bashFile = listOf(".bashrc", ".bash_profile").map { File(homeDir, it) }.firstOrNull { it.exists() }

        println("""
            Kinta has been successfully installed.
            
            To get started, type:

            $sourceCommand
        """.trimIndent())
        if (bashFile != null) {
            println("""
            # or if you want to add kinta to your path permanently: 
            echo '$sourceCommand' >> ${bashFile.absolutePath}
        """.trimIndent())
        }
        println("""
            
            Then type 'kinta' to get a list of commands.
        """.trimIndent())
    }
}