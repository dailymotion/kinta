package com.dailymotion.kinta.workflows.builtin

import com.dailymotion.kinta.Log
import com.dailymotion.kinta.integration.git.GitIntegration
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.github.ajalt.clikt.core.CliktCommand

val cleanLocal = object : CliktCommand(name = "cleanLocal", help = """
    Clean up branches in your local repository:
        - Remove remote tracking branches that have been deleted in the remote with git fetch -p
        - Remove all the local branches that only have closed or merged pull requests.
    This will force delete the local branches, you can restore deleted branches from your git reflog
    but that process is a bit more involved so double check before you call this workflow.
    This only works for repositories hosted on github.
""".trimIndent()) {
    override fun run() {
        GitIntegration.fetch(prune = true)
        val branchesInfo = GitIntegration.getBranches().filter { it != "master" }.map {
            GithubIntegration.getBranchInfo(branch = it)
        }
        val closedOrMergedBranches = branchesInfo.filter {
            val mergedOrClosedPullRequests = it.pullRequests.filter { it.closed || it.merged }
            it.pullRequests.isNotEmpty()
                    && it.pullRequests.size == mergedOrClosedPullRequests.size
        }.map { it.name }
        GitIntegration.deleteBranches(branches = closedOrMergedBranches, force = true)
        Log.d("Removed closed or merged branches:")
        closedOrMergedBranches.forEach {
            Log.d("* $it")
        }
    }
}