package com.sup.dev.java.tools

import com.sup.dev.java.libs.debug.err
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.xor

object ToolsCryptography {

    fun generateString(length: Int): String {
        val random = Random()
        val text = CharArray(length)
        for (i in 0 until length) {
            val c = random.nextInt(Character.MAX_CODE_POINT).toChar()
            text[i] = c
        }
        return String(text)
    }

    fun md5(st: String): String {
        var digest = ByteArray(0)
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(ToolsText.toBytes(st))
            digest = messageDigest.digest()
        } catch (e: NoSuchAlgorithmException) {
            err(e)
        }

        val bigInt = BigInteger(1, digest)
        var md5Hex = bigInt.toString(16)
        while (md5Hex.length < 32) {
            md5Hex = "0$md5Hex"
        }
        return md5Hex
    }

    fun encode(pText: String, pKey: String): ByteArray {
        val txt = ToolsText.toBytes(pText)
        val key = ToolsText.toBytes(pKey)
        val res = ByteArray(pText.length)

        for (i in txt.indices)
            res[i] = (txt[i] xor key[i % key.size])

        return res
    }

    fun decode(pText: ByteArray, pKey: String): String {
        val res = ByteArray(pText.size)
        val key = ToolsText.toBytes(pKey)

        for (i in pText.indices)
            res[i] = (pText[i] xor key[i % key.size])
        return ToolsText.toString(res)
    }

}
