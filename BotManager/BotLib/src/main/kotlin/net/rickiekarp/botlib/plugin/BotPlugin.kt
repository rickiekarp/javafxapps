package net.rickiekarp.botlib.plugin

import net.rickiekarp.botlib.runner.BotRunner

/**
 * Interface for the plugin classes.
 */
interface BotPlugin {

    /**
     * Performs the plugin layout node action.
     */
    fun setLayout(runner: BotRunner<*, *>)

    /**
     * Performs the plugin action.
     */
    fun run(runner: BotRunner<*, *>)
}
