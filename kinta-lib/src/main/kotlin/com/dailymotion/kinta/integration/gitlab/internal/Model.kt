package com.dailymotion.kinta.integration.gitlab.internal

import kotlinx.serialization.Serializable


@Serializable
data class Branch(
        val name: String
)

@Serializable
data class MergeRequest(
        val iid: Int,
        val state: String
)

@Serializable
data class MergeRequestBody(
        val source_branch: String,
        val target_branch: String,
        val title: String,
        val description: String,
)