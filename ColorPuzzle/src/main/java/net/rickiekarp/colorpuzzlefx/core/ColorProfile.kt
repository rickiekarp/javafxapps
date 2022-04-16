package net.rickiekarp.colorpuzzlefx.core

import javafx.scene.paint.Color
import java.util.*

class ColorProfile {
    internal val profile: MutableMap<Colors, Color> = EnumMap(Colors::class.java)
    fun getProfile(): Map<Colors, Color> {
        return Collections.unmodifiableMap(profile)
    }

    fun getColor(colors: Colors): Color? {
        return profile[colors]
    }

    init {
        profile[Colors.Color1] = Color.rgb(135, 206, 250) // light blue
        profile[Colors.Color2] = Color.rgb(155, 205, 50) // green
        profile[Colors.Color3] = Color.rgb(238, 221, 130) // yellow
        profile[Colors.Color4] = Color.rgb(244, 164, 96) // orange
        profile[Colors.Color5] = Color.rgb(255, 99, 71) // red
        profile[Colors.Color6] = Color.rgb(160, 82, 45) // brown
        profile[Colors.Color7] = Color.rgb(139, 136, 120) // grey
    }
}