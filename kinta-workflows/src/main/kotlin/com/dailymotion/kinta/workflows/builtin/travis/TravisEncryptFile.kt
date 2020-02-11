package com.dailymotion.kinta.workflows.builtin.travis

import com.dailymotion.kinta.integration.crypto.Crypto
import com.dailymotion.kinta.integration.crypto.toHex
import com.dailymotion.kinta.integration.travis.TravisIntegration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

object TravisEncryptFile : CliktCommand(name = "encryptFile") {
    val input by option("--input", help ="the file to encrypt").required()
    val repo by option("--repo", help ="The repository to use for encryption in the form owner/name").required()


    fun String.toVar(): String {
        return toUpperCase().replace(".", "_").replace(Regex("[^A-Z_]"), "")
    }

    override fun run() {
        val repoOwner = repo.split("/")[0]
        val repoName = repo.split("/")[1]

        val result = Crypto.aesCbcEncrypt(File(input).readBytes())

        val output = "$input.enc"

        File(output).writeBytes(result.ouptut)

        val keyVar = File(input).name.toVar() + "_KEY"
        val ivVar = File(input).name.toVar() + "_IV"
        val encryptedKey = TravisIntegration.encrypt(value = "$keyVar=${result.key.toHex()}", repoOwner = repoOwner, repoName = repoName)
        val encryptedIv = TravisIntegration.encrypt(value = "$ivVar=${result.iv.toHex()}", repoOwner = repoOwner, repoName = repoName)

        println("Encryption successful.")
        println("\nCommit this file to your repository, it contains the encrypted result:")
        println(output)
        println("\nAdd the decryption parameters to your .travis.yml:")
        println("- secure: $encryptedKey")
        println("- secure: $encryptedIv")
        println("\nWhen you want to decrypt your file, run:")
        println("openssl aes-256-cbc -K \$$keyVar -iv \$$ivVar -in $output -out $input -d")
    }
}