package com.dailymotion.kinta.integration.git.model

/**
 * @param name: the name of the branch
 * @param pullRequests: the pull requests that have `name` as HEAD
 * @param dependentPullRequests: the pull requests that have `name` as BASE
 */
data class BranchInfo(
        val name: String,
        val pullRequests: List<PullRequestInfo>,
        val dependentPullRequests: List<PullRequestInfo>
)

data class PullRequestInfo(
        val number: Int,
        val merged: Boolean,
        val closed: Boolean
)