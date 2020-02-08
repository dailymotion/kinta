package com.dailymotion.kinta.workflows

import com.dailymotion.kinta.Project
import com.dailymotion.kinta.Workflows
import com.dailymotion.kinta.integration.googleplay.internal.GetPlayStoreMetadata
import com.dailymotion.kinta.integration.googleplay.internal.GooglePlayIntegration
import com.dailymotion.kinta.integration.googleplay.internal.InitPlayStoreConfig
import com.dailymotion.kinta.workflows.builtin.GitCleanLocal
import com.dailymotion.kinta.workflows.builtin.GitCleanRemote
import com.dailymotion.kinta.workflows.builtin.GitPR
import com.dailymotion.kinta.workflows.builtin.playstore.*
import com.github.ajalt.clikt.core.CliktCommand

class BuiltInWorkflows : Workflows {

    private val gitToolWorkflows = listOf(
            GitPR,
            GitCleanLocal,
            GitCleanRemote
    )

    //Play Store relatives workflows
    private val playStoreWorkflows = mutableListOf(
            GetPlayStoreMetadata,
            GetPlayStorePreviews,
            UpdatePlayStoreListings,
            UpdatePlayStoreImages,
            UpdatePlayStoreChangeLogs,
            PublishPlayStore
    )

    override fun all() : List<CliktCommand> {
        val list = mutableListOf<CliktCommand>()
        if(GooglePlayIntegration.isConfigured()){
            list.addAll(playStoreWorkflows)
        }else{
            list.add(InitPlayStoreConfig)
        }
        Project.gitTool?.let {
            if(it.isConfigured()){
                list.addAll(gitToolWorkflows)
            }else{
                list.add(it.getSetUpWorkflow())
            }
        }
        return list
    }
}

