package net.rickiekarp.botter.botservice

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.net.NetResponse
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.ui.tray.ToolTrayIcon
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.botlib.BotLauncher
import net.rickiekarp.botlib.PluginConfig
import net.rickiekarp.botlib.model.PluginData
import net.rickiekarp.botlib.net.BotNetworkApi
import net.rickiekarp.botter.settings.AppConfiguration
import net.rickiekarp.botter.view.MainLayout
import javafx.application.Platform
import javafx.concurrent.Task
import net.rickiekarp.botlib.enums.BotPlatforms
import net.rickiekarp.botlib.enums.BotType

import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class BotTask(botLauncher: BotLauncher, private val plugin: PluginData) : Task<Void>() {
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var isTimerTaskRunning: Boolean = false
    private var countdownInMillis: Int = 0

    private var launcher: BotLauncher? = null

    override fun call(): Void? {
        if (canExecutePlugin(plugin)) {
            try {
                launcher!!.startAppiumService()
                if (launcher!!.isServiceRunning) {
                    when (plugin.pluginType) {
                        BotPlatforms.WEB -> when (PluginConfig.botType) {
                            BotType.Bot.FIREFOX -> launcher!!.launch(plugin)
                            BotType.Bot.CHROME -> launcher!!.launch(plugin)
                        }
                        BotPlatforms.ANDROID -> launcher!!.launch(plugin)
                    }
                } else {
                    println("There was a problem while starting the service!")
                    this.cancel()
                }
            } catch (e: Exception) {
                println("Something went wrong! Check the logs for more details!")
                LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e))
                this.cancel()
            }

        } else {
            this.cancel()
        }
        return null
    }

    init {
        this.launcher = botLauncher
        isTimerTaskRunning = false

        if (AppConfiguration.canBotRunPeriodical) {
            countdownInMillis = AppConfiguration.runInterval * 1000 * 60
        }

        // add an exceptionProperty ChangeListener to the task to throw exceptions from the Task.call() method
        //        if (DebugHelper.DEBUGVERSION) {
        //            botTask.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
        //                if(newValue != null) {
        //                    Exception ex = (Exception) newValue;
        //                    ex.printStackTrace();
        //                }
        //            });
        //        }

        val listThread = Thread(this)
        listThread.isDaemon = true

        this.setOnSucceeded { event1 ->
            println("Bot finished!")
            LogFileHandler.logger.info("Bot finished!")
            MainLayout.mainLayout!!.setStatus("success", "Finished")
            MainLayout.mainLayout!!.setLoadBarVisible(false)
            MainLayout.mainLayout!!.switchMode()
            launcher!!.stopAppiumService()
            DebugHelper.profile("stop", "BotAction")
            launcher = null

            if (AppConfiguration.canBotRunPeriodical) {
                LogFileHandler.logger.info("Starting bot timer! Countdown: " + countdownInMillis + "ms")
                MainLayout.mainLayout!!.switchTimeBox()
                timer = Timer()
                timerTask = object : TimerTask() {
                    override fun run() {
                        isTimerTaskRunning = true
                        if (!AppConfiguration.canBotRunPeriodical) {
                            this.cancel()
                            timer!!.cancel()
                            MainLayout.mainLayout!!.switchTimeBox()
                            isTimerTaskRunning = false
                        }

                        if (!MainScene.mainScene.windowScene!!.win.windowStage.stage.isShowing && !Configuration.showTrayIcon) {
                            this.cancel()
                            timer!!.cancel()
                        }

                        val hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(countdownInMillis.toLong()),
                                TimeUnit.MILLISECONDS.toMinutes(countdownInMillis.toLong()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countdownInMillis.toLong())),
                                TimeUnit.MILLISECONDS.toSeconds(countdownInMillis.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countdownInMillis.toLong())))

                        Platform.runLater {
                            //System.out.println("Timer: " + hms);
                            MainLayout.mainLayout!!.updateCountdown("" + hms)
                        }

                        if (Configuration.showTrayIcon && countdownInMillis == 15000) {
                            ToolTrayIcon.icon.displayTrayMessage()
                        }

                        if (countdownInMillis == 0) {
                            this.cancel()
                            timer!!.cancel()
                            isTimerTaskRunning = false
                            Platform.runLater {
                                MainLayout.mainLayout!!.switchTimeBox()
                                MainLayout.mainLayout!!.loadBot()
                            }
                        }
                        countdownInMillis -= 1000
                    }
                }
                timer!!.schedule(timerTask, 0, 1000)
            }
        }

        this.setOnCancelled {
            LogFileHandler.logger.info("Bot stopped!")
            MainLayout.mainLayout!!.setStatus("neutral", "Cancelled")
            MainLayout.mainLayout!!.setLoadBarVisible(false)
            MainLayout.mainLayout!!.switchMode()

            if (timer != null) {
                timer!!.cancel()
                MainLayout.mainLayout!!.switchTimeBox()
            }
            launcher!!.stopAppiumService()
            DebugHelper.profile("stop", "BotAction")
            launcher = null
        }

        this.setOnFailed {
            LogFileHandler.logger.warning("Bot failed!")
            MainLayout.mainLayout!!.setStatus("fail", "Failed")
            MainLayout.mainLayout!!.setLoadBarVisible(false)
            MainLayout.mainLayout!!.switchMode()
            launcher!!.stopAppiumService()
            DebugHelper.profile("stop", "BotAction")
            launcher = null
        }

        MainLayout.mainLayout!!.setStatus("neutral", "Running")
        DebugHelper.profile("start", "BotAction")

        //start the task in a new thread
        listThread.start()
    }

    fun resetTimer() {
        if (isTimerTaskRunning) {
            timerTask!!.cancel()
            timer!!.cancel()
            MainLayout.mainLayout!!.switchTimeBox()
        }
    }

    /**
     * Whether the bot is allowed to execute the currently selected plugin
     * @param plugin Plugin to validate
     * @return true, if the plugin is allowed by the server
     */
    private fun canExecutePlugin(plugin: PluginData): Boolean {
        return NetResponse.getResponseResultAsBoolean(AppContext.context.networkApi.runNetworkAction(BotNetworkApi.requestValidation(plugin)), "result")
    }
}
