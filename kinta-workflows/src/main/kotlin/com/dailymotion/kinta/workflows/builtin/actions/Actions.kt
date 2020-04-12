package com.dailymotion.kinta.workflows.builtin.actions

import com.dailymotion.console.console
import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.util.*

object ConfigureKeystore : CliktCommand(name = "configureKeystore", help = "configure keystore secrets in github actions. You can later use retrieveKeystore on the CI.") {
    override fun run() {
        val keystore = console.promptFile("Path to keystore:")
        /*val keystorePassword = console.prompt("Keystore password:")
        val keyAlias = console.prompt("Key alias:")
        val keyPassword = console.prompt("Key password:")*/

        GithubIntegration.setSecret(
            name = KintaEnv.Var.KINTA_KEYSTORE.name,
            value = Base64.encode(keystore.readBytes())
        )
    }
}

object SetSecret : CliktCommand(name = "setSecret", help = "adds a secret") {
    val secretName by option().required()
    val value by option().required()

    override fun run() {
        GithubIntegration.setSecret(
            name = secretName,
            value = value
        )
    }
}

object DeleteSecret : CliktCommand(name = "deleteSecret", help = "deletes a secret") {
    val secretName by option().required()

    override fun run() {
        GithubIntegration.deleteSecret(
            name = secretName
        )
    }
}


val actions = object : CliktCommand(name = "actions") {
    override fun run() {

    }
}.subcommands(ConfigureKeystore,
    SetSecret,
    DeleteSecret)

