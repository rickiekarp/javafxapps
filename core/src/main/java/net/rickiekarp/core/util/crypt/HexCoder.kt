package net.rickiekarp.core.util.crypt

import okhttp3.internal.and

object HexCoder {

    fun bytesToHex(data: ByteArray): String {
        val buf = StringBuilder()
        for (aData in data) {
            var halfbyte = aData.toInt().ushr(4) and 0x0F
            var two_halfs = 0
            do {
                if (halfbyte in 0..9)
                    buf.append(('0'.toInt() + halfbyte).toChar())
                else
                    buf.append(('a'.toInt() + (halfbyte - 10)).toChar())
                halfbyte = aData and 0x0F
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }
}
