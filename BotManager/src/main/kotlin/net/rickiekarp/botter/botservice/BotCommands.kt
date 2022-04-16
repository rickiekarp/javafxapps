package net.rickiekarp.botter.botservice

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.model.ConsoleCommands
import net.rickiekarp.core.settings.AppCommands
import net.rickiekarp.botter.settings.AppConfiguration
import net.rickiekarp.botter.view.MainLayout

/**
 * This class is used to define additional application related console commands.
 * Simply add the new command to the commands list and define the method to execute.
 */
class BotCommands {
    @Throws(NoSuchMethodException::class)
    fun addBotCommands() {
        AppCommands.commandsList.add(
                ConsoleCommands("/setInterval", "<minutes>", LanguageController.getString("timer_desc")) {
                    setInterval("30") // set fixed interval until parameters are supported
                })
    }

    fun setInterval(test: String) {
        AppConfiguration.runInterval = Integer.parseInt(test)
        MainLayout.mainLayout!!.setStatus("neutral", "Timer intervall: " + AppConfiguration.runInterval + " min")
    }
}
