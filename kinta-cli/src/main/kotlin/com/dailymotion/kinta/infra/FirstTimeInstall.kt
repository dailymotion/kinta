package com.dailymotion.kinta.infra

import com.dailymotion.kinta.infra.Constants.currentDir
import com.dailymotion.kinta.infra.Constants.kintaDir
import java.io.File

object FirstTimeInstall {
    fun run() {
        val initFile = File(kintaDir, "setup-environment.sh")

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