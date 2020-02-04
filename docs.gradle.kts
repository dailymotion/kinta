#!/usr/bin/env kscript

val GRADLE_GROUP = "documentation"

val mkdocsYmlTask = tasks.register("mkdocsYml") {
  group = GRADLE_GROUP

  dependsOn(subprojects.map {
    it.tasks.named("dokka") }
  )

  val outputDir = file("docs/kdoc/")
  val inputDir = file("build/kdoc")

  val mkdocsYml = file("mkdocs.yml")
  val mkdocsYmlTemplate = file("mkdocs.yml.template")

  outputs.files(mkdocsYml)
  outputs.dir(outputDir)
  inputs.files(mkdocsYmlTemplate)
  inputs.dir(inputDir)

  doLast {
    outputDir.deleteRecursively()
    inputDir.copyRecursively(outputDir)

    val kdocEntries = outputDir
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

    mkdocsYml.writeText(
        mkdocsYmlTemplate.readText().replace(
            "KdocPlaceholder",
            kdocEntries.joinToString("\n", prefix = "\n", postfix = "\n")
        )
    )
  }
}

val indexTask = tasks.register("indexMd") {
  group = GRADLE_GROUP

  val outputFile = file("docs/index.md")
  val inputFile = file("README.md")

  inputs.files(inputFile)

  doLast {
    inputFile.readLines()
        .filter { !it.contains("project website") } // Remove the link to the project website for users landing on github
        .map { it.replace("docs/", "") } // Fix links
        .joinToString(separator = "\n", postfix = "\n")
        .let {
          outputFile.writeText(it)
        }
  }
}

val copyZipTask = tasks.register("copyZip", Copy::class.java) {
  from(subprojects.first { it.name == "kinta-cli" }.tasks.named("distZip").flatMap { (it as Zip).archiveFile })
  into("docs")
  rename {
    "kinta.zip"
  }
}

val deployTask = tasks.register("deployDocs", Exec::class.java) {
  group = GRADLE_GROUP

  dependsOn(indexTask)
  dependsOn(mkdocsYmlTask)
  dependsOn(copyZipTask)

  commandLine = "mkdocs gh-deploy".split(" ")
}
