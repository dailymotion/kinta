val DOCUMENTATION_GROUP = "documentation"

fun cleanDocs() {
    listOf("archives",
            "docs/kdoc",
            "docs/zip",
            "docs/index.md",
            "mkdocs.yml",
            "docs/README.md").forEach {
        file(it).deleteRecursively()
    }
}

fun retrieveArchives() {
    runCommand(command = "git clone --depth 1 https://github.com/dailymotion/kinta -b archives archives")

    File("archives/").copyRecursively(target = File("docs"), overwrite = true)
    File("archives").deleteRecursively()
}

fun copyCurrentKdoc() {
    file("build/kdoc").copyRecursively(File("docs/kdoc/master"))
}

fun determineLatestZip() {
    val latestZip = file("docs/zip").listFiles().sortedBy { it.name }.lastOrNull()

    if (latestZip != null) {
        latestZip.copyTo(File("docs/zip/latest.zip"))
    }
}

fun createIndexMd() {
    val outputFile = file("docs/index.md")
    val inputFile = file("README.md")

    inputFile.readLines()
            .filter { !it.contains("project website") } // Remove the link to the project website for users landing on github
            .map { it.replace("docs/", "") } // Fix links
            .joinToString(separator = "\n", postfix = "\n")
            .let {
                outputFile.writeText(it)
            }
}

fun createMkdocsYml() {
    val outputDir = file("docs/kdoc/")

    val mkdocsYml = file("mkdocs.yml")
    val mkdocsYmlTemplate = file("mkdocs.yml.template")

    /**
     * Iterate over the different kdoc directories and creates the mkdocs.yml index
     */
    val kdocEntries = outputDir
            .listFiles()
            .filter { it.isDirectory }
            .sortedByDescending { it.name }
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

val cleanDocs = tasks.register("cleanDocs") {
    group = DOCUMENTATION_GROUP

    doLast {
        cleanDocs()
    }
}


val deployTask = tasks.register("deployDocs") {
    group = DOCUMENTATION_GROUP

    dependsOn(subprojects.map {
        it.tasks.named("dokka")
    })
    dependsOn(subprojects.first { it.name == "kinta-cli" }.tasks.named("distZip"))

    doLast {
        cleanDocs()
        retrieveArchives()
        copyCurrentKdoc()
        determineLatestZip()
        createIndexMd()
        createMkdocsYml()

        runCommand(command = "mkdocs gh-deploy --verbose")
    }
}

fun runCommand(workingDirectory: File = File("."), command: String) {
    runCommand(workingDirectory = workingDirectory, args = *command.split(" ").toTypedArray())
}

fun runCommand(workingDirectory: File = File("."), vararg args: String) {
    println(args.joinToString(","))
    val process = ProcessBuilder().command(*args)
            .directory(workingDirectory)
            .redirectErrorStream(true)
            .start()
    val output = process.inputStream
    while(true) {
        // Somehow bufferedReader.readLine() deosn't work
        val b = ByteArray(200)
        val read = output.read(b)

        if (read < 0) {
            break
        }
        System.out.print(String(b, 0, read))
    }
    val ret = process.waitFor()
    check(ret == 0) {
        "Command '${args.joinToString(",")}' failed with exit code: $ret"
    }
}

val deployArchives = tasks.register("deployArchives") {
    group = DOCUMENTATION_GROUP

    val distZipTaskProvider = subprojects.first { it.name == "kinta-cli" }.tasks.named("distZip").map { it as Zip }

    dependsOn(distZipTaskProvider)

    dependsOn(subprojects.map {
        it.tasks.named("dokka")
    })

    doLast {
        runCommand(command = "git clone --depth 1 https://github.com/dailymotion/kinta -b archives archives")

        File("archives/kdoc/").mkdirs()
        File("build/kdoc").copyRecursively(File("archives/kdoc/${project.version}"))

        val distZip = distZipTaskProvider.get().archiveFile.get().asFile

        distZip.copyTo(File("archives/zip/${distZip.name}"))

        runCommand(File("archives"), "git", "add", ".")
        runCommand(File("archives"), "git", "commit", "-m", "Adding archives for version ${project.version}")
        runCommand(File("archives"), "git", "push", "origin", "archives")

        File("archives").deleteRecursively()
    }
}
