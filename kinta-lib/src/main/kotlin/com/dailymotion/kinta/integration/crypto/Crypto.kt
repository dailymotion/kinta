package com.dailymotion.kinta.integration.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object Crypto {
    fun aesCbcEncrypt(input: ByteArray, key: ByteArray, iv: ByteArray): ByteArray? {
        require(key.size == 32) {
            "key must be 32 bytes long"
        }
        require(iv.size == 16) {
            "iv must be 16 bytes long"
        }

        val ivspec = IvParameterSpec(iv)

        val secretKey = SecretKeySpec(key, "AES")

        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec)

        return cipher.doFinal(input)
    }

    class EncryptionResult(val ouptut: ByteArray, val key: ByteArray, val iv: ByteArray)

    fun aesCbcEncrypt(input: ByteArray): EncryptionResult {
        val secureRandom = SecureRandom()
        val iv = ByteArray(16)
        secureRandom.nextBytes(iv)

        val key = ByteArray(32)
        secureRandom.nextBytes(key)

        return EncryptionResult(aesCbcEncrypt(input, key, iv)!!, key, iv)
    }
}

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

fun String.toBin() : ByteArray {

    val result = ByteArray(length / 2)

    for (i in 0 until length step 2) {
        val firstIndex = HEX_CHARS.indexOf(this[i].toUpperCase());
        val secondIndex = HEX_CHARS.indexOf(this[i + 1].toUpperCase());

        val octet = firstIndex.shl(4).or(secondIndex)
        result.set(i.shr(1), octet.toByte())
    }

    return result
}
