package com.dailymotion.kinta.integration.travis

import com.dailymotion.kinta.globalJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object TravisIntegration {
    /**
     * https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail
     * This method will fail if the PEM contains several objects.
     * For our case, it's enough since Travis only sends us one single public key.
     */
    private fun decodeSingleObjectPem(pem: String): ByteArray {
        val base64 = pem.trim().lines().drop(1).dropLast(1).joinToString("")

        check(!base64.contains("-----")) {
            "It looks like the provided PEM file contained several objects. This is not supported at the moment."
        }
        return Base64.getDecoder().decode(base64)
    }

    fun getPublicKey(repoOwner: String, repoName: String): String {
        return "https://api.travis-ci.org/repos/$repoOwner/$repoName/key".let {
            Request.Builder().get().url(it)
        }.let {
            OkHttpClient().newCall(it.build()).execute().body()!!.string()
        }.let {
            globalJson.parseToJsonElement(it).jsonObject["key"]?.jsonPrimitive?.content!!
        }
    }

    fun encrypt(repoOwner: String, repoName: String, value: String): String {
        val publicKey = getPublicKey(repoOwner, repoName)

        val cipher = Cipher.getInstance("RSA")

        val key = X509EncodedKeySpec(decodeSingleObjectPem(publicKey))
                .let {
                    KeyFactory.getInstance("RSA").generatePublic(it)
                }
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return cipher.doFinal(value.toByteArray()).let {
            Base64.getEncoder().encodeToString(it)
        }
    }
}