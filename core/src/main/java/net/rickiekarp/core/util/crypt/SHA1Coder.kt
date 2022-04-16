package net.rickiekarp.core.util.crypt

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SignatureException

object SHA1Coder {

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
    fun getSHA1(p: String): ByteArray {
        val md: MessageDigest
        md = MessageDigest.getInstance("SHA1")
        md.update(p.toByteArray(charset("utf-8")))
        return md.digest()
    }

    fun getSHA1Bytes(data: String, isHMAC: Boolean): ByteArray {
        var sha1 = ByteArray(0)
        try {
            sha1 = getSHA1(data)
            if (isHMAC) {
                sha1 = HMACCoder.encode(data, sha1)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        }

        return sha1
    }

    fun getSHA1String(digest: ByteArray): String {
        val sb = StringBuilder(digest.size * 2)
        var s: String
        for (b in digest) {
            s = Integer.toHexString(0xFF and b.toInt())
            sb.append(s)
        }
        return sb.toString()
    }
}
