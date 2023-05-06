package net.rickiekarp.botlib.plugin

import javafx.application.Platform
import net.rickiekarp.botlib.model.PluginData
import net.rickiekarp.botlib.runner.BotRunner
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import java.io.File
import java.net.URLClassLoader
import java.security.Policy

object PluginExecutor {

    @Throws(Exception::class)
    fun executeLayoutSetter(runner: BotRunner<*, *>, plugin: PluginData) {
        Policy.setPolicy(PluginPolicy())
        System.setSecurityManager(SecurityManager())

        val authorizedJarFile = File(Configuration.config.pluginDirFile.toString() + File.separator + plugin.pluginName + ".jar")
        val authorizedLoader: ClassLoader
        authorizedLoader = URLClassLoader.newInstance(arrayOf(authorizedJarFile.toURI().toURL()))
        val authorizedBotPlugin: BotPlugin
        authorizedBotPlugin = authorizedLoader.loadClass(plugin.pluginClazz.get()).newInstance() as BotPlugin
        authorizedBotPlugin.setLayout(runner)
    }

    @Throws(Exception::class)
    fun executePlugin(runner: BotRunner<*, *>, plugin: PluginData) {
        Policy.setPolicy(PluginPolicy())
        System.setSecurityManager(SecurityManager())

        val authorizedJarFile = File(Configuration.config.pluginDirFile.toString() + File.separator + plugin.pluginName + ".jar")
        val authorizedLoader: ClassLoader
        authorizedLoader = URLClassLoader.newInstance(arrayOf(authorizedJarFile.toURI().toURL()))
        val authorizedBotPlugin: BotPlugin
        LogFileHandler.logger.info("Starting " + plugin.pluginType + " bot - " + plugin.pluginName + " (" + plugin.pluginOldVersion + ")")
        authorizedBotPlugin = authorizedLoader.loadClass(plugin.pluginClazz.get()).newInstance() as BotPlugin
        authorizedBotPlugin.run(runner)
    }

    fun executePlugin(plugin: PluginData) {
        Policy.setPolicy(PluginPolicy())
        System.setSecurityManager(SecurityManager())

        try {
            val authorizedJarFile = File(Configuration.config.pluginDirFile.toString() + File.separator + plugin.pluginName + ".jar")
            val authorizedLoader: ClassLoader
            authorizedLoader = URLClassLoader.newInstance(arrayOf(authorizedJarFile.toURI().toURL()))
            val authorizedPlugin: Plugin
            authorizedPlugin = authorizedLoader.loadClass(plugin.pluginClazz.get()).newInstance() as Plugin
            Platform.runLater { authorizedPlugin.run() }
        } catch (e: Exception) {
            if (DebugHelper.DEBUG) {
                e.printStackTrace()
            } else {
                ExceptionHandler(e)
            }
        }

    }
}
