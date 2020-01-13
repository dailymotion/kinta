package com.dailymotion.kinta

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