package com.dailymotion.kinta.helper

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object Sha1 {
    private val hexArray = "0123456789ABCDEF".toCharArray()
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun compute(inputStream: InputStream): String {
        val buffer = ByteArray(8192)
        val sha1digest = MessageDigest.getInstance("SHA-1")
        DigestInputStream(inputStream, sha1digest).use {
            while (it.read(buffer) != -1);
        }
        return bytesToHex(sha1digest.digest())
    }
}