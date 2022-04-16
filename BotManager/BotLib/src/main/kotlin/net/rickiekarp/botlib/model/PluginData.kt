package net.rickiekarp.botlib.model

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.botlib.enums.BotPlatforms
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class PluginData(pluginClazz: String?, pluginName: String?, pluginOldVersion: String?, pluginNewVersion: String?, platform: BotPlatforms?) {

    val pluginClazz: SimpleStringProperty = SimpleStringProperty(pluginClazz)
    val pluginName: SimpleStringProperty = SimpleStringProperty(pluginName)
    val pluginOldVersion: SimpleStringProperty = SimpleStringProperty(pluginOldVersion)
    private val pluginNewVersion: SimpleStringProperty = SimpleStringProperty(pluginNewVersion)
    var pluginType: BotPlatforms? = null
        private set
    private val pluginUpdateEnable: SimpleBooleanProperty
    private val pluginPackage: SimpleStringProperty
    private val pluginActvity: SimpleStringProperty
    var pluginCredentials: Credentials? = null

    var updateEnable: Boolean
        get() = pluginUpdateEnable.get()
        set(enable) = pluginUpdateEnable.set(enable)

    init {
        this.pluginType = platform
        this.pluginUpdateEnable = SimpleBooleanProperty()
        this.pluginPackage = SimpleStringProperty() //android only
        this.pluginActvity = SimpleStringProperty() //android only
    }

    fun getPluginClazz(): String {
        return pluginClazz.get()
    }

    fun setPluginClazz(clazz: String) {
        this.pluginClazz.set(clazz)
    }

    fun getPluginOldVersion(): String {
        return pluginOldVersion.get()
    }

    fun setPluginOldVersion(version: String) {
        this.pluginOldVersion.set(version)
    }

    fun getPluginNewVersion(): String {
        return pluginNewVersion.get()
    }

    fun setPluginNewVersion(version: String) {
        this.pluginNewVersion.set(version)
    }

    fun setBotType(type: BotPlatforms) {
        pluginType = type
    }

    fun setNewEditButtonName(): String? {
        //if local plugin does not exist, show download button
        if (pluginOldVersion.get() == null) {
            return LanguageController.getString("download")
        } else if (pluginNewVersion.get() == null) {
            //show nothing

            //if remote version is newer than the local one
        } else {
            try {
                if (Integer.parseInt(pluginNewVersion.get().replace(".", "")) > Integer.parseInt(pluginOldVersion.get().replace(".", ""))) {
                    return LanguageController.getString("update")
                }
            } catch (e: NumberFormatException) {
                return null
            }

        }
        return null
    }

    fun getPluginPackage(): String {
        return pluginPackage.get()
    }

    fun setPluginPackage(name: String) {
        pluginPackage.set(name)
    }

    fun getPluginActvity(): String {
        return pluginActvity.get()
    }

    fun setPluginActvity(activity: String) {
        pluginActvity.set(activity)
    }

    companion object {
        var pluginData = FXCollections.observableArrayList<PluginData>()
    }
}

