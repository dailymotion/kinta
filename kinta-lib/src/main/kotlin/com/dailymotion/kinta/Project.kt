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
    val projectDir by lazy { getBaseDir() }

    fun file(path: String): File = File(projectDir, path)

    private fun isBaseDir(dir: File) = dir.list().contains(".kinta")

    fun getBaseDir(): File {
        val dir = findBaseDir()
        check(dir != null) {
            "Cannot find a .kinta directory"
        }
        return dir
    }

    fun findBaseDir(): File? {
        var dir = File(".")

        while (!isBaseDir(dir)) {
            if (dir.parent == null) {
                return null
            }
            dir = File(dir.parent)
        }

        return dir
    }
}