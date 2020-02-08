package com.dailymotion.kinta.workflows.builtin

import com.dailymotion.kinta.Project
import com.dailymotion.kinta.integration.git.GitIntegration
import com.github.ajalt.clikt.core.CliktCommand

object GitCleanLocal : CliktCommand(name = "gitCleanLocal", help = """
    Clean up branches in your local repository:
        - Remove remote tracking branches that have been deleted in the remote with git fetch -p
        - Remove all the local branches that only have closed or merged pull requests.
    This will force delete the local branches, you can restore deleted branches from your git reflog
    but that process is a bit more involved so double check before you call this workflow.
    This only works for repositories hosted on github.
""".trimIndent()) {
    override fun run() {
        val gitTool = Project.gitTool ?: throw Exception("Your git is not supported yet")

        GitIntegration.fetch(prune = true)
        val branchesInfo = GitIntegration.getBranches()
                .filter { it != "master" }
                .map { gitTool.getBranchInfo(branch = it) }

        val branchesToDelete = branchesInfo.filter {
            if (it.pullRequests.isNullOrEmpty()) {
                // This ref has no associated pull request yet, don't delete it
                return@filter false
            }

            if (it.pullRequests.count { !it.merged && !it.closed } > 0) {
                // There is one open pull request on this ref, don't delete it
                return@filter false
            }

            if (it.dependantPullRequests.count { !it.merged && !it.closed } > 0) {
                // This ref is used as a base for another one, don't delete it
                return@filter false
            }

            // fallthrough, delete this branch
            true
        }.map {
            it.name
        }

        GitIntegration.deleteBranches(branchesToDelete)
    }
}