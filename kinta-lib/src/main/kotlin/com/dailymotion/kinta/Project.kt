package com.dailymotion.kinta

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

object Project {

    val repository by lazy {
        val root = findParentContaining(".git")
        FileRepositoryBuilder().setGitDir(File(root, ".git"))
                .readEnvironment()
                .findGitDir()
                .build()!!
    }

    val git by lazy {
        Git(repository)
    }

    val projectDir by lazy { getBaseDir() }

    fun file(path: String): File = File(projectDir, path)

    fun getBaseDir(): File {
        val dir = findBaseDir()
        check(dir != null) {
            "Cannot find a .kinta directory"
        }
        return dir
    }

    fun findParentContaining(dirName: String): File? {
        var dir = File(".")

        while (true) {
            if (dir.listFiles().find { it.isDirectory && it.name == dirName } != null) {
                return dir
            }
            if (dir.parent == null) {
                return null
            }
            dir = File(dir.parent)
        }
    }
    fun findBaseDir(): File? {
        return findParentContaining(".kinta")
    }
}