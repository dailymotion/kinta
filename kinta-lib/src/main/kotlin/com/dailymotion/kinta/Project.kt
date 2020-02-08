package com.dailymotion.kinta

import com.dailymotion.kinta.integration.github.GithubIntegration
import com.dailymotion.kinta.integration.gitlab.GitlabIntegration
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import kotlin.system.exitProcess

object Project {

    val repository by lazy {
        FileRepositoryBuilder().setGitDir(file(".git"))
                .readEnvironment()
                .findGitDir()
                .build()!!
    }

    val gitTool: GitTool? by lazy {
        val remoteConfigList = git.remoteList().call()
        val uri = remoteConfigList.filter { it.name == "origin" }.first().urIs[0]
        when (uri.host) {
            "gitlab.com" -> GitlabIntegration
            "github.com" -> GithubIntegration
            else -> null
        }
    }

    val git by lazy {
        Git(repository)
    }
    val projectDir by lazy { findBaseDir() }

    fun file(path: String): File = File(projectDir, path)

    private fun isBaseDir(dir: File): Boolean {
        return dir.listFiles().firstOrNull {
            it.name == "kintaSrc"
                    && it.isDirectory
        } != null
    }

    fun findBaseDir(): File? {
        var dir = File(".")

        while (!isBaseDir(dir)) {
            if (dir.parent == null) {
                Log.e("Cannot find kintaSrc directory, please run 'kinta init'")
                exitProcess(1)
            }
            dir = File(dir.parent)
        }

        return dir
    }
}