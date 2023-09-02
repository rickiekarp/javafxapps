package crypt.custom

import net.rickiekarp.core.enums.CustomCoderType
import net.rickiekarp.core.model.CustomCoderConfig
import net.rickiekarp.core.util.crypt.Base64Coder
import net.rickiekarp.core.util.crypt.ColorCoder
import net.rickiekarp.core.util.crypt.SHA1Coder
import net.rickiekarp.core.util.crypt.custom.CustomCoderV1
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException

internal class CustomCoderV1Test {

    @Test
    fun testEncode() {
        val expected = "ШΞoЛЛ"
        val coderConfig = CustomCoderConfig(
            CustomCoderType.V1,
            "",
            false
        )

        val actual = CustomCoderV1.encode("input", coderConfig)
        assertEquals(expected, actual)
    }

    @Test
    fun testDecode() {
        val expected = "INPUT"
        val coderConfig = CustomCoderConfig(
            CustomCoderType.V1,
            "",
            false
        )

        val actual = CustomCoderV1.decode("ШΞoЛЛ", coderConfig)
        assertEquals(expected, actual)
    }
}
