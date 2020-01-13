#!/usr/bin/env kscript

import java.io.File
import kotlin.contracts.contract

fun runCommand(command: String) {
    val code = ProcessBuilder()
            .command(command.split(" "))
            .inheritIO()
            .start()
            .waitFor()

    if (code != 0) {
        System.exit(1)
    }
}

val version = File("build.gradle.kts").readLines()
        .mapNotNull {
            val m = Regex(" *version *= *\"(.*)\"").matchEntire(it)

            m?.groupValues?.get(1)
        }.firstOrNull()

checkNotNull(version) {
    "cannot find version in build.gradle.kts"
}

runCommand("./gradlew dokka")
File("docs/kdoc/current").deleteRecursively()
File("build/kdoc").copyRecursively(File("docs/kdoc/current"))

val kdocEntries = File("docs/kdoc")
        .listFiles()
        .filter { it.isDirectory }
        .sortedBy { it.name }
        .flatMap { version ->
            listOf("      - ${version.name}:") +
                    version.listFiles()
                            .filter { it.isDirectory }
                            .sortedBy { it.name }
                            .map { module ->
                                "          - ${module.name}: kdoc/${version.name}/${module.name}/index.md"
                            }
        }

//println(kdocEntries.joinToString("\n"))

File("mkdocs.yml").writeText(
        File("mkdocs.yml.template").readText().replace(
                "KdocPlaceholder",
                kdocEntries.joinToString("\n", prefix = "\n", postfix = "\n")
        )
)

File("docs/index.md").delete()
File("README.md").readLines()
        .filter { !it.contains("project website") } // Remove the link to the project website for users landing on github
        .map { it.replace("docs/", "") } // Fix links
        .joinToString(separator = "\n", postfix = "\n")
        .let {
            File("docs/index.md").writeText(it)
        }

runCommand("mkdocs gh-deploy")
