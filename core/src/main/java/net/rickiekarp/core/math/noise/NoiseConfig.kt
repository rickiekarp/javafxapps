package net.rickiekarp.core.math.noise

class NoiseConfig(
    var width: Int,
    var height: Int,
    var frequency: Int,
    var timeIncrement: Double = 0.01,
    var redMultiplier: Int,
    var greenMultiplier: Int,
)