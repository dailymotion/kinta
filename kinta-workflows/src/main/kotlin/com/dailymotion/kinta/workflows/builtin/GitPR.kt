package com.dailymotion.kinta.workflows.builtin

import com.dailymotion.kinta.Project
import com.github.ajalt.clikt.core.CliktCommand

object GitPR : CliktCommand(name = "gitPR", help = "Open a pull request on Gitlab") {

    override fun run() {
        Project.gitTool?.openPullRequest()
    }
}