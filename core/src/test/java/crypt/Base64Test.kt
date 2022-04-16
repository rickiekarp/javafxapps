package crypt

import net.rickiekarp.core.util.crypt.Base64Coder
import net.rickiekarp.core.util.crypt.ColorCoder
import net.rickiekarp.core.util.crypt.SHA1Coder
import org.junit.jupiter.api.Test

import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException

import org.junit.jupiter.api.Assertions.assertEquals

internal class Base64Test {

    @Test
    fun testBase64() {
        val expected = "qUqP5cyxm6YcTAhz05Hph5gvu9M="
        var actual: String? = null
        try {
            actual = String(Base64Coder.encode(SHA1Coder.getSHA1("test")))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun testBase64ColorEncryption() {
        val expectedArray = arrayOf("BkwhAj5ecq9VCUCllR0nSh/IQis=", //blue
                "xrTeNSjy+dVTQJdLNqkJfltOwMI=", //black
                "fYZAdbAvmtXZ3VWGI3DyD4rfqE4=", //green
                "qwkBRtBiesEpLx1wIuggRRL4m90=", //orange
                "xSVUSnzYZWVg8CDBlw8TRx6UEH4=", //red
                "MupRuyeVaySDvBhLnaEjE/BDC1M=", //yellow
                "wuZXSMa8HnhY3RBSqMlFHIa21YA=", //purple
                "Jl/ZJm/2UVFG6JgKxvHbv+q7qTc="  //cyan
        )
        var actual: String? = null
        for (i in ColorCoder.colorArray.indices) {
            println(ColorCoder.colorArray[i].toString())
            try {
                actual = String(Base64Coder.encode(SHA1Coder.getSHA1(ColorCoder.colorArray[i].toString())))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            assertEquals(expectedArray[i], actual)
        }
    }
}
