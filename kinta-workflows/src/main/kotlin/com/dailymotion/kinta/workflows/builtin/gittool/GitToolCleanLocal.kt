package com.dailymotion.kinta.workflows.builtin.gittool

import com.dailymotion.kinta.GitTool
import com.dailymotion.kinta.Logger
import com.dailymotion.kinta.integration.git.GitIntegration
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.dailymotion.kinta.integration.gitlab.GitlabIntegration
import com.github.ajalt.clikt.core.CliktCommand

object GithubCleanLocal : GitCleanLocal(GithubIntegration)
object GitlabCleanLocal : GitCleanLocal(GitlabIntegration)

open class GitCleanLocal(val gitTool: GitTool) : CliktCommand(name = "cleanLocal", help = """
    Clean up branches in your local repository:
        - Remove remote tracking branches that have been deleted in the remote with git fetch -p
        - Remove all the local branches that only have closed or merged pull requests.
    This will force delete the local branches, you can restore deleted branches from your git reflog
    but that process is a bit more involved so double check before you call this workflow.
    This only works for repositories hosted on github.
""".trimIndent()) {
    override fun run() {
        GitIntegration.fetch(prune = true)
        Logger.i("Fetching branchs infos, Please wait...")
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

            if (it.dependentPullRequests.count { !it.merged && !it.closed } > 0) {
                // This ref is used as a base for another one, don't delete it
                return@filter false
            }

            // fallthrough, delete this branch
            true
        }.map {
            it.name
        }
        Logger.i("Deleting ${branchesToDelete.count()} local branchs...")
        GitIntegration.deleteBranches(branchesToDelete, force = true)
        Logger.i("Clean local done!")
    }
}