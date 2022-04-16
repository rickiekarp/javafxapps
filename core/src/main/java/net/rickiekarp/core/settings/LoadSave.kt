package net.rickiekarp.core.settings

import net.rickiekarp.core.controller.LanguageController
import javafx.geometry.Side
import javafx.scene.paint.Color

/** Annotation for indicating load/save a field.  */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class LoadSave {
    companion object {
        /** settings defaults  */
        @JvmField val host = "https://api.rickiekarp.net/"
        @JvmField val updateChannel = 0
        @JvmField val language = LanguageController.locale
        @JvmField val themeState = 0
        @JvmField val colorScheme = 0
        @JvmField val animations = true
        @JvmField val useSystemBorders = false
        @JvmField val logState = false
        @JvmField val showTrayIcon = false

        /** advanced settings defaults  */
        @JvmField val debugState = false
        @JvmField val decorationColor = Color.valueOf("#1d1d1d")
        @JvmField val shadowColorFocused = Color.valueOf("#000000")
        @JvmField val shadowColorNotFocused = Color.valueOf("#a9a9a9")
        @JvmField val tabPosition = Side.LEFT
    }
}
