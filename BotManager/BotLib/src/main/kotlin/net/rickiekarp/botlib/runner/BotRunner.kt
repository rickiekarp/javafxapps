package net.rickiekarp.botlib.runner

import net.rickiekarp.botlib.model.Credentials
import javafx.scene.Node
import javafx.scene.control.Button

/**
 * This class starts the bot on the defined platform.
 * Supported browsers: chrome, firefox
 * Supported mobile platforms: android
 * @param <DRI> Driver
 * @param <SVC> Driver service
</SVC></DRI> */
abstract class BotRunner<DRI, SVC> {
    private var `object`: DRI? = null

    var driverService: SVC? = null


    //--- Credentials
    var credentials: Credentials? = null

    fun get(): DRI? {
        return `object`
    }

    internal fun set(obj: DRI) {
        this.`object` = obj
    }

    abstract fun start()

    abstract fun setLayout(layoutNode: Node)

    abstract fun addSetting(title: String, description: String, isVisible: Boolean, settingNode: Node)

    abstract fun addControlButton(vararg controlButton: Button)
    abstract fun removeControlButton(index: Int)
    fun hasCredentials(): Boolean {
        return credentials != null
    }
}
