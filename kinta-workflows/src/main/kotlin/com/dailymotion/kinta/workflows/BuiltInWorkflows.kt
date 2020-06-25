package com.dailymotion.kinta.workflows

import com.dailymotion.kinta.KintaEnv
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.dailymotion.kinta.integration.github.internal.GithubInit
import com.dailymotion.kinta.integration.gitlab.GitlabIntegration
import com.dailymotion.kinta.integration.gitlab.internal.GitlabInit
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.dailymotion.kinta.workflows.builtin.actions.actions
import com.dailymotion.kinta.workflows.builtin.bitrise.BitriseTrigger
import com.dailymotion.kinta.workflows.builtin.env.envCommand
import com.dailymotion.kinta.workflows.builtin.gittool.*
import com.dailymotion.kinta.workflows.builtin.playstore.*
import com.dailymotion.kinta.workflows.builtin.travis.Travis
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import kotlin.system.exitProcess

object BuiltInWorkflows {

    val playStoreWorkflows = object : CliktCommand(name = "playstore", help = "Edit your app listing and releases on the Google Play Store.") {
        override fun run() {
            if (context.invokedSubcommand != PlayStoreInit && !GooglePlayIntegration.isConfigured()) {
                println("Your environment is not set up. Please run kinta $commandName ${PlayStoreInit.commandName}")
                exitProcess(0)
            }
        }
    }.subcommands(listOf(
        PlayStoreInit,
            PlayStorePullMetadatas,
            PlayStorePullPreviews,
            PlayStoreUpdateListings,
            PlayStoreUpdateImages,
            PlayStoreUpdateChangeLogs,
            PlayStorePublish
    ))

    val gitHubWorkflows = object : CliktCommand(name = "github", help = "Manage your Github pull requests.") {
        override fun run() {
        }
    }.subcommands(listOf(
            GithubInit,
            GithubPR,
            GithubCleanLocal,
            GithubCleanRemote
    ))

    val gitlabWorkflows = object : CliktCommand(name = "gitlab", help = "Manage your Gitlab pull requests.") {
        override fun run() {
        }
    }.subcommands(listOf(
            GitlabInit,
            GitlabPR,
            GitlabCleanLocal,
            GitlabCleanRemote
    ))

    val bitriseWorkflows = object : CliktCommand(name = "bitrise", help = "Interact with your Bitrise workflows.") {
        override fun run() {
            KintaEnv.getOrFail(KintaEnv.Var.BITRISE_PERSONAL_TOKEN)
        }
    }.subcommands(listOf(BitriseTrigger))

    fun all(): List<CliktCommand> =
            listOf(
                    playStoreWorkflows,
                    gitHubWorkflows,
                    gitlabWorkflows,
                    Travis,
                    bitriseWorkflows,
                    actions,
                    envCommand
            )
}

