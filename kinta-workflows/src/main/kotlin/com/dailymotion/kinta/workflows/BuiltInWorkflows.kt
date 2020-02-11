package com.dailymotion.kinta.workflows

import com.dailymotion.kinta.Workflows
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.dailymotion.kinta.workflows.builtin.crypto.AesEncrypt
import com.dailymotion.kinta.workflows.builtin.travis.Travis
import com.dailymotion.kinta.integration.github.internal.GithubInit
import com.dailymotion.kinta.integration.gitlab.GitlabIntegration
import com.dailymotion.kinta.integration.gitlab.internal.GitlabInit
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.dailymotion.kinta.integration.googleplay.internal.PlayStoreInit
import com.dailymotion.kinta.integration.googleplay.internal.PlayStorePullMetadatas
import com.dailymotion.kinta.workflows.builtin.gittool.*
import com.dailymotion.kinta.workflows.builtin.playstore.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import kotlin.system.exitProcess

class BuiltInWorkflows : Workflows {

    private val playStoreWorkflows = object : CliktCommand(name = "playstore", help = "Play Store relative workflows") {
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

    private val gitHubWorkflows = object : CliktCommand(name = "github", help = "Github relative workflows") {
        override fun run() {
            if (context.invokedSubcommand != GithubInit && !GithubIntegration.isConfigured()) {
                println("Your environment is not set up. Please run kinta $commandName ${GithubInit.commandName}")
                exitProcess(0)
            }
        }
    }.subcommands(listOf(
            GithubInit,
            GithubPR,
            GithubCleanLocal,
            GithubCleanRemote
    ))

    private val gitlabWorkflows = object : CliktCommand(name = "gitlab", help = "Gitlab relative workflows") {
        override fun run() {
            if (context.invokedSubcommand != GitlabInit && !GitlabIntegration.isConfigured()) {
                println("Your environment is not set up. Please run kinta $commandName ${GitlabInit.commandName}")
                exitProcess(0)
            }
        }
    }.subcommands(listOf(
            GitlabInit,
            GitlabPR,
            GitlabCleanLocal,
            GitlabCleanRemote
    ))

    override fun all(): List<CliktCommand> =
            listOf(
                    playStoreWorkflows,
                    gitHubWorkflows,
                    gitlabWorkflows,
                    Travis,
                    AesEncrypt
            )
}

