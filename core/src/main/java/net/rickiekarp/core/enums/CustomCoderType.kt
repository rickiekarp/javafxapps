package net.rickiekarp.core.enums

enum class CustomCoderType(private val seed: Long) {
    V1(0),
    V2(4358922007433029608);

    fun getDefaultSeed() = seed
}