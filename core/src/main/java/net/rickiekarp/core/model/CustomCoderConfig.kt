package net.rickiekarp.core.model

import net.rickiekarp.core.enums.AlphabetType
import net.rickiekarp.core.enums.CustomCoderType
import net.rickiekarp.core.math.noise.PerlinNoise2D

class CustomCoderConfig(
    var coderType: CustomCoderType,
    var baseSeed: String,
    var characterSetConfig: MutableMap<AlphabetType, Boolean>,
    var preserveWhiteSpaces: Boolean = false,
    var noiseGenerator: PerlinNoise2D? = null
)