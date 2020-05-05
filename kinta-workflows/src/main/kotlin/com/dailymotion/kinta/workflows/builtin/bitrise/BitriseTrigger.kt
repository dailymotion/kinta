package com.dailymotion.kinta.workflows.builtin.bitrise

import com.dailymotion.kinta.Project
import com.dailymotion.kinta.integration.bitrise.Bitrise
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

object BitriseTrigger : CliktCommand(name = "trigger", help = "Create and checkout a branch for working on the given jira issue. Will set the ticket state to 'In Progress'.") {

    private val workflowId by argument("workflowId").optional()

    private val branchName by option("--branch").default("master")

    override fun run() {
        if (workflowId == null) {
            println("\nYou have to specify workflow_id : kinta trigger [WORKFLOW_ID]\nAvailables ones for your repo are :\n\n")
            Bitrise.getAvailableWorkflows(repoName = Project.repositoryName()).forEach { println(it) }
            println("\n")
        } else {
            Bitrise.triggerBuild(
                    repoName = Project.repositoryName(),
                    workflowId = workflowId!!,
                    branch = branchName
            )
        }
    }
}