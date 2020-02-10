package com.dailymotion.kinta.workflows.builtin.gittool

import com.dailymotion.kinta.GitTool
import com.dailymotion.kinta.integration.github.GithubIntegration
import com.dailymotion.kinta.integration.gitlab.GitlabIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

object GithubCleanRemote: GitCleanRemote(GithubIntegration)
object GitlabCleanRemote: GitCleanRemote(GitlabIntegration)

open class GitCleanRemote(val gitTool: GitTool) : CliktCommand(name = "cleanRemote", help = """
    Clean up branches in your git repository.""".trimIndent()) {

    private val dontAsk by option("--dont-ask").flag()

    override fun run() {
        val branchesInfo = gitTool.getAllBranches()
                .filter { it != "master" }
                .map { gitTool.getBranchInfo(branch = it) }

        val branchesToDelete = branchesInfo.filter {
            val name = it.name

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

            if (!dontAsk) {
                println("You are going to delete : $name Continue [yes/no]?")
                loop@ while (true) {
                    when (readLine()) {
                        "yes" -> break@loop
                        "no" -> return@filter false
                    }
                }
            }

            // fallthrough, delete this branch
            true
        }.map {
            it.name
        }

        branchesToDelete.forEach {
            gitTool.deleteRef(ref = it)
        }
    }
}