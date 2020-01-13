package com.dailymotion.kinta.workflows.builtin

import com.dailymotion.kinta.integration.github.GithubIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

val cleanGithubBranches = object : CliktCommand(name = "cleanGithubBranches", help = """
    Clean up branches in your github repository. This is useful for workflows where 
        - Remove remote tracking branches that have been deleted in the remote with git fetch -p
        - Remove all the local branches that only have closed or merged pull requests.
    This will force delete the local branches, you can restore deleted branches from your git reflog
    but that process is a bit more involved so double check before you call this workflow.
    This only works for repositories hosted on github.
""".trimIndent()) {

    private val dontAsk by option("--dont-ask").flag()

    override fun run() {
        val branchesInfo = GithubIntegration.getAllBranches().map { GithubIntegration.getBranchInfo(branch = it) }

        val branchesToDelete = branchesInfo.filter {
            val name = it.name

            if (name == "master") {
                // never delete master
                return@filter false
            }

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
            GithubIntegration.deleteRef(ref = it)
        }
    }
}