package net.rickiekarp.core.util.crypt

import net.rickiekarp.core.enums.CustomCoderType
import net.rickiekarp.core.util.crypt.custom.CustomCoderV1
import net.rickiekarp.core.util.crypt.custom.CustomCoderV2

object CustomCoder {

    fun encode(input: String, baseSeed: String, coderVersion: CustomCoderType) : String {
        return when (coderVersion) {
            CustomCoderType.V1 -> { CustomCoderV1.encode(input, baseSeed) }
            CustomCoderType.V2 -> { CustomCoderV2.encode(input, baseSeed) }
        }
    }

    fun decode(input: String, baseSeed: String, coderVersion: CustomCoderType) : String {
        return when (coderVersion) {
            CustomCoderType.V1 -> { CustomCoderV1.decode(input, baseSeed) }
            CustomCoderType.V2 -> { CustomCoderV2.decode(input, baseSeed) }
        }
    }
}