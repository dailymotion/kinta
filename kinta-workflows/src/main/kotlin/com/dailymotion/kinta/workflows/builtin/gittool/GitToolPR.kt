package com.dailymotion.kinta.workflows.builtin.gittool

import com.dailymotion.kinta.GitTool
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.dailymotion.kinta.integration.gitlab.GitlabIntegration
import com.github.ajalt.clikt.core.CliktCommand

object GithubPR: GitPR(GithubIntegration)
object GitlabPR: GitPR(GitlabIntegration)

open class GitPR(val gitTool: GitTool) : CliktCommand(name = "pr", help = "Open a pull request on Gitlab") {

    override fun run() {
        gitTool.openPullRequest()
    }
}