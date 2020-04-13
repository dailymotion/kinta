package com.dailymotion.kinta.integration.android

import com.dailymotion.kinta.KintaEnv
import java.io.File
import java.util.*

@Suppress("NAME_SHADOWING")
object AndroidIntegration {
    fun signApk(
        input: File,
        output: File,
        keystore: File? = null,
        keystorePassword: String? = null,
        keyAlias: String? = null,
        keyPassword: String? = null
    ) {
        val file = File.createTempFile("keystore", null)
        try {
            val keystore = keystore ?: let {
                val base64 = KintaEnv.get(KintaEnv.Var.KINTA_KEYSTORE)
                file.writeBytes(Base64.getDecoder().decode(base64))
                file
            }
            val keystorePassword = keystorePassword ?: KintaEnv.get(KintaEnv.Var.KINTA_KEYSTORE_PASSWORD)
            val keyAlias = keyAlias ?: KintaEnv.get(KintaEnv.Var.KINTA_KEY_ALIAS)
            val keyPassword = keyPassword ?: KintaEnv.get(KintaEnv.Var.KINTA_KEY_PASSWORD)

            val code = ProcessBuilder("which", "apksigner")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()

            val apksignerCommand = if (code == 0) {
                "apksigner"
            } else {
                val androidHome = System.getenv("ANDROID_HOME")

                check(androidHome != null) {
                    "make sure apksigner is in your path or that \$ANDROID_HOME is set"
                }
                val command = File(androidHome).walkTopDown().firstOrNull {
                    it.name == "apksigner"
                }?.absolutePath

                check (command != null) {
                    "Cannot find apksigner in $androidHome"
                }
                command
            }

            //println("found apksigner at $apksignerCommand")

            ProcessBuilder(
                apksignerCommand,
                "sign",
                "--ks",
                keystore.absolutePath,
                "--ks-pass",
                "pass:$keystorePassword",
                "--ks-key-alias",
                keyAlias,
                "--key-pass",
                "pass:$keyPassword",
                "--out",
                output.absolutePath,
                input.absolutePath
            )
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
        } finally {
            file.delete()
        }
    }
}