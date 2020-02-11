package com.dailymotion.kinta.workflows.builtin.travis

import com.dailymotion.kinta.integration.travis.TravisIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

object TravisEncrypt : CliktCommand(name = "encrypt", help = "Encrypt the given value for Travis CI") {
    val repo by option("--repo", help ="The repository to use for encryption in the form owner/name").required()
    val value by argument()

    override fun run() {
        val repoOwner = repo.split("/")[0]
        val repoName = repo.split("/")[1]

        println(TravisIntegration.encrypt(repoOwner, repoName, value))
    }
}