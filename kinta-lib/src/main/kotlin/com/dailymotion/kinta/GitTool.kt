package com.dailymotion.kinta

import com.dailymotion.kinta.integration.git.model.BranchInfo

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
            remote: String = "origin",
            branch: String): BranchInfo

    fun openPullRequest(
            token: String? = null,
            owner: String? = null,
            repo: String? = null,
            head: String? = null,
            base: String? = null,
            title: String? = null,
            body: String? = null,
    ): String?

    fun deleteRef(
            token: String? = null,
            owner: String? = null,
            repo: String? = null,
            ref: String
    )

    fun setAssignee(
        token: String? = null,
        owner: String? = null,
        repo: String? = null,
        issue: String,
        assignees: List<String>
    )
}