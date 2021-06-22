package com.dailymotion.kinta.integration.gradle

import com.dailymotion.kinta.Logger
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.GradleProject
import java.io.File


class Gradle(directory: File? = null) {

    private var gradleConnector = GradleConnector
            .newConnector()
            .forProjectDirectory(directory ?: File("."))


    fun executeTask(task: String, args: List<String> = emptyList()) = executeTasks(tasks = arrayOf(task), args = args)

    fun executeTasks(vararg tasks: String, args: List<String> = emptyList()) = runGradleBuild { projectConnection ->
        logExecuteTasks(*tasks, args = args)
        projectConnection.newBuild().forTasks(*tasks).withArguments(args)
    }

    fun executeGroupTasks(groupName: String, vararg tasks: String, args: List<String> = emptyList()) = runGradleBuild { projectConnection ->
        val rootProjectModel = projectConnection.getModel(GradleProject::class.java)

        val taskList = mutableListOf(*tasks)
        logExecuteTasks(*tasks)
        rootProjectModel.children.forEach { subProject ->
            val subProjectGroupTaskList = subProject.tasks.filter { it.group == groupName }.map { it.name }
            taskList.addAll(subProjectGroupTaskList)
        }
        logExecuteGroupTasks(groupName, taskList.subList(tasks.size, taskList.size), args = args)
        projectConnection.newBuild().forTasks(*taskList.toTypedArray()).withArguments(args)
    }

    private fun logExecuteTasks(vararg taskName: String, args: List<String> = emptyList()) {
        taskName.forEach {
            Logger.i("\n==> Executing task: $it")
        }
        if(args.isNotEmpty()) {
            Logger.i(args.joinToString(separator = ", "))
        }
    }

    private fun logExecuteGroupTasks(groupName: String, taskList: List<String>, args: List<String> = emptyList()) {
        Logger.i("\n==> List of task to execute for group: $groupName")
        Logger.i("==> {")
        taskList.forEach {
            Logger.i("==>\t${it}")
        }
        Logger.i("==> }")
        if(args.isNotEmpty()) {
            Logger.i(args.joinToString(separator = ", "))
        }
    }

    private fun runGradleBuild(gradleLambda: (ProjectConnection) -> BuildLauncher): Int {
        val projectConnection = gradleConnector.connect()
        return try {
            var buildLauncher = gradleLambda(projectConnection)
            buildLauncher = if (Logger.level >= Logger.LEVEL_INFO) {
                buildLauncher.addArguments("-q")
            } else {
                buildLauncher.addArguments("--stacktrace")
            }

            buildLauncher
                    .setStandardOutput(System.out)
                    .setStandardError(System.err)
                    .run()
            0
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        } finally {
            projectConnection.close()
        }
    }
}