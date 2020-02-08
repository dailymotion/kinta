package com.dailymotion.kinta

import com.dailymotion.kinta.integration.git.model.BranchInfo
import com.github.ajalt.clikt.core.CliktCommand

interface GitTool {

    fun getAllBranches(
            token: String? = null,
            owner: String? = null,
            repo: String? = null
    ): List<String>

    fun getBranchInfo(
            token: String? = null,
            owner: String? = null,
            repo: String? = null,
            branch: String): BranchInfo

    fun openPullRequest(token: String? = null,
                        owner: String? = null,
                        repo: String? = null,
                        head: String? = null,
                        base: String? = null,
                        title: String? = null
    )

    fun deleteRef(
            token: String? = null,
            owner: String? = null,
            repo: String? = null,
            ref: String
    )

    fun isConfigured(): Boolean

    fun getSetUpWorkflow(): CliktCommand
}