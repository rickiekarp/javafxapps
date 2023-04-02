package net.rickiekarp.botlib

import javafx.collections.ObservableList
import net.rickiekarp.botlib.enums.BotPlatforms
import net.rickiekarp.botlib.enums.BotType
import net.rickiekarp.botlib.plugin.BotSetting
import net.rickiekarp.core.settings.LoadSave

object PluginConfig {

    var settingsList: ObservableList<BotSetting>? = null

    var botPlatform: BotPlatforms? = null
    var botType: BotType.Bot? = null

    /** settings  */
    @LoadSave
    var browserProfileName: String? = null
    @LoadSave
    var chromeConfigDirectory: String? = null

    var isBrowserBotPlugin = true
}
