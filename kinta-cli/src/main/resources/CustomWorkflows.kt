package com.dailymotion.kinta

import com.github.ajalt.clikt.core.CliktCommand

/**
 * This is where you can register every custom workflow you will use with Kinta
 * You can use every integrations supported (Have a look to kinta/lib)
 * Feel free to share workflows to kinta/workflows-builtin if you think it could be useful for the community!
 */
class CustomWorkflows : Workflows {
    override fun all(): List<CliktCommand> {
        return listOf(object: CliktCommand(name = "customWorkflowHelloWorld ") {
            override fun run() {
                println("This is an Hello World custom workflow.")
                // code your workflow here
            }
        })
    }
}