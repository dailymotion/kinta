package com.dailymotion.kinta.integration.git.model

/**
 * @param name: the name of the branch
 * @param pullRequests: the pull requests associated with a given branch
 */
data class BranchInfo(
        val name: String,
        val pullRequests: List<PullRequestInfo>,
        val dependantPullRequests: List<PullRequestInfo>
)

data class PullRequestInfo(
        val number: Int,
        val merged: Boolean,
        val closed: Boolean
)