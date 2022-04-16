package net.rickiekarp.core.util.crypt

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.SignatureException

internal object HMACCoder {
    private const val HMAC_SHA1_ALGORITHM = "HmacSHA1"

    @Throws(SignatureException::class)
    fun encode(data: String, key: ByteArray): ByteArray {
        try {
            // get an hmac_sha1 key from the raw key bytes
            val signingKey = SecretKeySpec(key, HMAC_SHA1_ALGORITHM)

            // get an hmac_sha1 Mac instance and initialize with the signing key
            val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
            mac.init(signingKey)

            return mac.doFinal(data.toByteArray())
        } catch (e: Exception) {
            throw SignatureException("Failed to generate HMAC : " + e.message)
        }
    }

    @Throws(SignatureException::class)
    fun encode(data: String, key: String): String {
        try {
            val digest = encode(data, key.toByteArray())
            val sb = StringBuilder(digest.size * 2)
            var s: String
            for (b in digest) {
                s = Integer.toHexString(0xFF and b.toInt())
                sb.append(s)
            }
            return sb.toString()
        } catch (e: Exception) {
            throw SignatureException("Failed to generate HMAC : " + e.message)
        }
    }
}
