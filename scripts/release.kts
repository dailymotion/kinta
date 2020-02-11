#!/usr/bin/env kscript
import java.io.File

/**
 * A script to run locally in order to make a release.
 *
 * You need kscript installed on your machine: https://github.com/holgerbrandl/kscript
 *
 * To edit, run `kscript --idea misc/release.kts`
 */

fun runCommand(vararg args: String): String {
    val builder = ProcessBuilder(*args)
            .redirectError(ProcessBuilder.Redirect.INHERIT)

    val process = builder.start()
    val ret = process.waitFor()

    val output = process.inputStream.bufferedReader().readText()
    if (ret != 0) {
        throw java.lang.Exception("command ${args.joinToString(" ")} failed:\n$output")
    }

    return output
}

fun setCurrentVersion(version: String) {
    val file = File("build.gradle.kts")
    var newContent = file.readLines().map {
        it.replace(Regex("^version = .*"), "version = $version")
    }.joinToString(separator = "\n", postfix = "\n")
    file.writeText(newContent)
}

fun getCurrentVersion(): String {
    val file = File("build.gradle.kts")
    var version = file.readLines().mapNotNull {
        Regex("^version = (.*)").matchEntire(it)?.groupValues?.get(1)
    }.firstOrNull()

    require(version != null) {
        "cannot find the version in ${file.name}"
    }

    return version
}

fun getNext(version: String, position: Int) = version.split(".").mapIndexed { index, s ->
    when {
        index == position -> (s.toInt() + 1).toString()
        index > position -> "0"
        else -> s
    }
}.joinToString(".")

fun getNextPatch(version: String) = getNext(version, 2)
fun getNextMinor(version: String) = getNext(version, 1)
fun getNextMajor(version: String) = getNext(version, 0)

if (runCommand("git", "status", "--porcelain").isNotEmpty()) {
    println("Your git repo is not clean. Make sur to stash or commit your changes before making a release")
    System.exit(1)
}

val version = getCurrentVersion()
val nextPatch = getNextPatch(version)
val nextMinor = getNextMinor(version)
val nextPatchAfterMinor = getNextPatch(nextMinor)
val nextMajor = getNextMajor(version)
val nextPatchAfterMajor = getNextPatch(nextMajor)

var tagVersion: String = ""

while (tagVersion.isEmpty()) {
    println("Current version is '$version-SNAPSHOT'.")
    println("1. patch: tag $version and bump to $nextPatch")
    println("2. minor: tag $nextMinor and bump to $nextPatchAfterMinor")
    println("3. major: tag $nextMajor and bump to $nextPatchAfterMajor")
    println("What do you want to do [1/2/3]?")

    val answer = readLine()!!.trim()
    when (answer) {
        "1" -> tagVersion = version
        "2" -> tagVersion = nextMinor
        "3" -> tagVersion = nextMajor
    }
}

setCurrentVersion(tagVersion)

runCommand("git", "commit", "-a", "-m", "release $tagVersion")
runCommand("git", "tag", "v$tagVersion")

val snapshot = "${getNextPatch(tagVersion)}-SNAPSHOT"
setCurrentVersion(snapshot)
runCommand("git", "commit", "-a", "-m", "version is now $snapshot")

println("Everything is done. Verify everything is ok and type `git push origin master` to trigger the new version.")
