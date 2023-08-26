package net.rickiekarp.core.model

import net.rickiekarp.core.enums.CustomCoderType

class CustomCoderConfig(
    var coderType: CustomCoderType,
    var baseSeed: String,
    var preserveWhiteSpaces: Boolean = false
)