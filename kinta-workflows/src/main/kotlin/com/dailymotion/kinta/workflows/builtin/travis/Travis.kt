package com.dailymotion.kinta.workflows.builtin.travis

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object Travis : CliktCommand(help = "Encrypt variables and files for Travis CI.") {
    override fun run() {

    }

    init {
        subcommands(TravisEncrypt,
                TravisEncryptFile)
    }
}