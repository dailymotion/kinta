package com.dailymotion.kinta.workflows

import com.dailymotion.kinta.Workflows
import com.dailymotion.kinta.integration.github.internal.InitGithub
import com.dailymotion.kinta.integration.gitlab.internal.InitGitlab
import com.dailymotion.kinta.integration.googleplay.internal.GetPlayStoreMetadata
import com.dailymotion.kinta.integration.googleplay.internal.InitPlayStoreConfig
import com.dailymotion.kinta.workflows.builtin.gittool.*
import com.dailymotion.kinta.workflows.builtin.playstore.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class BuiltInWorkflows : Workflows {

    private val playStoreWorkflows = object : CliktCommand(name = "playstore", help = "Play Store relative workflows") {
        override fun run() {
        }
    }.subcommands(listOf(
            InitPlayStoreConfig,
            GetPlayStoreMetadata,
            PlayStorePullPreviews,
            PlayStoreUpdateListings,
            PlayStoreUpdateImages,
            PlayStoreUpdateChangeLogs,
            PlayStorePublish
    ))

    private val gitHubWorkflows = object : CliktCommand(name = "github", help = "Github relative workflows") {
        override fun run() {
        }
    }.subcommands(listOf(
            InitGithub,
            GithubPR,
            GithubCleanLocal,
            GithubCleanRemote
    ))

    private val gitlabWorkflows = object : CliktCommand(name = "gitlab", help = "Gitlab relative workflows") {
        override fun run() {
        }
    }.subcommands(listOf(
            InitGitlab,
            GitlabPR,
            GitlabCleanLocal,
            GitlabCleanRemote
    ))

    override fun all(): List<CliktCommand> =
            listOf(
                    playStoreWorkflows,
                    gitHubWorkflows,
                    gitlabWorkflows
            )
}

