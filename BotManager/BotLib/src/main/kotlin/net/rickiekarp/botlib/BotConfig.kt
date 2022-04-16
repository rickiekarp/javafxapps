package net.rickiekarp.botlib

import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.settings.LoadSave

import java.io.File

object BotConfig {
    @LoadSave
    var nodeBinary: String? = null
    var appiumBinary = File.separator + "appium" + File.separator + "build" + File.separator + "lib" + File.separator + "main.js"

    val modulesDirFile: File
        get() = File(Configuration.config.configDirFile.toString() + File.separator + "modules")

    var APPIUM_LOG_LEVEL = 1

    var DEVICE_NAME: String? = null
    var VERSION: String? = null
    var UDID: String? = null
}
