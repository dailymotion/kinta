package com.dailymotion.kinta.integration.gradle

import com.dailymotion.kinta.Logger
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.GradleProject
import java.io.File


class Gradle(directory: File? = null, val useLoggingLevelQuiet: Boolean = Logger.level > Logger.LEVEL_INFO) {

    private var gradleConnector = GradleConnector
            .newConnector()
            .forProjectDirectory(directory ?: File("."))


    fun executeTask(task: String) = executeTasks(task)

    fun executeTasks(vararg tasks: String) = runGradleBuild { projectConnection ->
        logExecuteTasks(*tasks)
        projectConnection.newBuild().forTasks(*tasks)

    }

    fun executeGroupTasks(groupName: String, vararg tasks: String) = runGradleBuild { projectConnection ->
        val rootProjectModel = projectConnection.getModel(GradleProject::class.java)

        val taskList = mutableListOf(*tasks)
        logExecuteTasks(*tasks)
        rootProjectModel.children.forEach { subProject ->
            val subProjectGroupTaskList = subProject.tasks.filter { it.group == groupName }.map { it.name }
            taskList.addAll(subProjectGroupTaskList)
        }
        logExecuteGroupTasks(groupName, taskList.subList(tasks.size, taskList.size))
        projectConnection.newBuild().forTasks(*taskList.toTypedArray())
    }

    private fun logExecuteTasks(vararg taskName: String) {
        taskName.forEach {
            Logger.i("\n==> Executing task: $it\n")
        }
    }

    private fun logExecuteGroupTasks(groupName: String, taskList: List<String>) {
        Logger.i("\n==> List of task to execute for group: $groupName")
        Logger.i("==> {")
        taskList.forEach {
            Logger.i("==>\t${it}")
        }
        Logger.i("==> }\n")
    }

    private fun runGradleBuild(gradleLambda: (ProjectConnection) -> BuildLauncher): Int {
        val projectConnection = gradleConnector.connect()
        return try {
            var buildLauncher = gradleLambda(projectConnection)
            buildLauncher = if (useLoggingLevelQuiet) {
                buildLauncher.withArguments("-q")
            } else {
                buildLauncher.withArguments("--stacktrace")
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