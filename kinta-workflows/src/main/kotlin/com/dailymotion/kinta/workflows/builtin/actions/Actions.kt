package com.dailymotion.kinta.workflows.builtin.actions

import com.dailymotion.console.console
import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File
import java.util.*

object ConfigureKeystore : CliktCommand(name = "configureKeystore", help = "configure keystore secrets in github actions. You can later use retrieveKeystore on the CI.") {
    override fun run() {
        val keystore = console.promptFile("Path to keystore:")
        val keystorePassword = console.prompt("Keystore password:")
        val keyAlias = console.prompt("Key alias:")
        val keyPassword = console.prompt("Key password:")

        console.println("encrypting and uploading secrets...")

        GithubIntegration.setSecret(
            name = KintaEnv.Var.KINTA_KEYSTORE.name,
            value = Base64.getEncoder().encodeToString(keystore.readBytes())
        )
        GithubIntegration.setSecret(
            name = KintaEnv.Var.KINTA_KEYSTORE_PASSWORD.name,
            value = keystorePassword
        )
        GithubIntegration.setSecret(
            name = KintaEnv.Var.KINTA_KEY_ALIAS.name,
            value = keyAlias
        )
        GithubIntegration.setSecret(
            name = KintaEnv.Var.KINTA_KEY_PASSWORD.name,
            value = keyPassword
        )

        console.println("4 secrets have been set in your Github repo. Add the following to .github/workflows/your_workflow_name.yml, in your step:")
        console.println("env:")

        listOf(
            KintaEnv.Var.KINTA_KEYSTORE.name,
            KintaEnv.Var.KINTA_KEYSTORE_PASSWORD.name,
            KintaEnv.Var.KINTA_KEY_ALIAS.name,
            KintaEnv.Var.KINTA_KEY_PASSWORD.name).forEach {
            console.println("  $it: \${{ secrets.$it }}")
        }
    }
}

object RetrieveKeystore : CliktCommand(name = "retrieveKeystore", help = "retrieves a keystore that has been set in Github secrets using configureKeystore") {
    val output by option(help = "path to the keystore file").default("keystore.jks")
    override fun run() {
        val keystore = KintaEnv.get(KintaEnv.Var.KINTA_KEYSTORE)
        check(keystore != null) {
            "No keystore found. Are you running on Github Actions/Did you callconfigureKeystore first?"
        }

        File(output).writeBytes(
            Base64.getDecoder().decode(keystore)
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

