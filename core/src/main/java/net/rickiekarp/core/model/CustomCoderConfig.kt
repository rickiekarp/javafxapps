package net.rickiekarp.core.model

import net.rickiekarp.core.enums.AlphabetType
import net.rickiekarp.core.enums.CustomCoderType

class CustomCoderConfig(
    var coderType: CustomCoderType,
    var baseSeed: String,
    var characterSetConfig: MutableMap<AlphabetType, Boolean>,
    var preserveWhiteSpaces: Boolean = false
)