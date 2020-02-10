package com.dailymotion.kinta.workflows.builtin.crypto

import com.dailymotion.kinta.integration.crypto.Crypto
import com.dailymotion.kinta.integration.crypto.toHex
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

object AesEncrypt : CliktCommand(name = "aesEncrypt") {
    val input by option("--input", help ="the file to encrypt").required()
    val output by option("--output", help = "the resulting file").required()
    val iv by option("--iv", help = "The IV used for encryption.").required()
    val key by option("--key", help = "The key used for encryption.").required()

    override fun run() {
        val result = Crypto.aesCbcEncrypt(File(input).readBytes())

        File(output).writeBytes(result.ouptut)
        File(key).writeBytes(result.key)
        File(iv).writeBytes(result.iv)

        println("File successfully encrypted")
        println("iv: ${result.iv.toHex()}")
        println("key: ${result.key.toHex()}")
    }
}