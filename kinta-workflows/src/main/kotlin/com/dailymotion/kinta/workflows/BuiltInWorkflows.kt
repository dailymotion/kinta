package com.dailymotion.kinta.workflows

import com.dailymotion.kinta.Workflows
import com.dailymotion.kinta.workflows.builtin.cleanGithubBranches
import com.dailymotion.kinta.workflows.builtin.cleanLocal
import com.dailymotion.kinta.workflows.builtin.playstore.*

class BuiltInWorkflows : Workflows {

    override fun all() = listOf(
            cleanLocal,
            cleanGithubBranches,
            GetPlayStoreMetadata,
            GetPlayStorePreviews,
            UpdatePlayStoreListings,
            UpdatePlayStoreImages,
            UpdatePlayStoreChangeLogs,
            PublishPlayStore
    )
}

