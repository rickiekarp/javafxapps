package net.rickiekarp.botlib

import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.util.OSValidator
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.botlib.model.PluginData
import net.rickiekarp.botlib.plugin.PluginExecutor
import net.rickiekarp.botlib.runner.BotRunner
import net.rickiekarp.botlib.runner.BotRunnerBuilder
import io.appium.java_client.remote.MobileCapabilityType
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag
import javafx.application.Platform
import net.rickiekarp.botlib.enums.BotPlatforms
import net.rickiekarp.botlib.enums.BotType
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.GeckoDriverService
import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.remote.DesiredCapabilities

import java.io.File
import java.io.IOException

class BotLauncher {

    private var appiumService: AppiumDriverLocalService? = null
    private var geckoDriverService: GeckoDriverService? = null
    private var chromeDriverService: ChromeDriverService? = null

    var isServiceRunning: Boolean = false
        private set

    fun createBotRunner(data: PluginData) {
        runnerInstance = BotRunnerBuilder().build(data)
    }

    /**
     * Sets up the capabilities and executes the bot
     */
    @Throws(Exception::class)
    fun launch(plugin: PluginData) {
        if (PluginConfig.isBrowserBotPlugin) {
            runnerInstance!!.credentials = plugin.pluginCredentials
            PluginExecutor.executePlugin(runnerInstance!!, plugin)
        } else {
            PluginExecutor.executePlugin(plugin)
        }
    }

    private fun bugCheck(): Boolean {
        when (PluginConfig.botPlatform) {
            BotPlatforms.ANDROID -> {
            }
            BotPlatforms.WEB -> if (PluginConfig.botType == null) {
                Platform.runLater { MessageDialog(0, "Invalid browser selection!", 500, 300) }
                return true
            }
        }
        return false
    }

    /**
     * Starts the appium server / web driver for the respective platform
     * @throws IOException IOException
     */
    @Throws(IOException::class)
    fun startAppiumService() {
        //check for available bot type
        if (bugCheck()) {
            return
        }

        val os = OSValidator.os
        var fileName: String? = null
        val driverFile: File

        when (PluginConfig.botType) {
            BotType.Bot.CHROME -> {
                if (os != null) {
                    when (os) {
                        OSValidator.OperatingSystem.WINDOWS -> fileName = "chromedriver.exe"
                        OSValidator.OperatingSystem.UNIX -> fileName = "chromedriver"
                        else -> fileName = "chromedriver"
                    }
                }

                driverFile = File(BotConfig.modulesDirFile.toString() + File.separator + "driver" + File.separator + fileName)
                if (driverFile.exists()) {
                    System.setProperty("webdriver.chrome.driver", driverFile.absolutePath)
                    chromeDriverService = ChromeDriverService.createDefaultService()
                    chromeDriverService!!.start()
                    runnerInstance!!.driverService = chromeDriverService as Nothing?
                    isServiceRunning = true
                    return
                } else {
                    Platform.runLater { MessageDialog(0, "Could not find appropriate driver!", 400, 200) }
                    isServiceRunning = false
                    return
                }
            }

            BotType.Bot.FIREFOX -> {
                if (os != null) {
                    when (os) {
                        OSValidator.OperatingSystem.WINDOWS -> fileName = "geckodriver.exe"
                        OSValidator.OperatingSystem.UNIX -> fileName = "geckodriver"
                        else -> fileName = "geckodriver"
                    }
                } else {
                    LogFileHandler.logger.warning("Could not detect OS!")
                    isServiceRunning = false
                    return
                }

                driverFile = File(BotConfig.modulesDirFile.toString() + File.separator + "driver" + File.separator + fileName)
                if (driverFile.exists()) {
                    System.setProperty("webdriver.gecko.driver", driverFile.absolutePath)

                    geckoDriverService = GeckoDriverService.Builder()
                            .usingAnyFreePort()
                            .usingDriverExecutable(driverFile)
                            .build()
                    geckoDriverService!!.start()
                    runnerInstance!!.driverService = geckoDriverService as Nothing?
                    isServiceRunning = true
                    return
                } else {
                    Platform.runLater { MessageDialog(0, "Could not find appropriate driver!", 400, 200) }
                    isServiceRunning = false
                    return
                }
            }

            BotType.Bot.ANDROID -> {
                val appiumLogLevel = arrayOf("error", "warn", "info", "debug")
                val appiumPath = File(BotConfig.modulesDirFile.toString() + File.separator + BotConfig.appiumBinary)
                if (appiumPath.exists()) {
                    appiumService = AppiumDriverLocalService.buildService(AppiumServiceBuilder()
                            .usingDriverExecutable(File(BotConfig.nodeBinary))
                            .withAppiumJS(appiumPath)
                            .withIPAddress("127.0.0.1")
                            .usingAnyFreePort()
                            .withArgument(GeneralServerFlag.LOG_LEVEL, appiumLogLevel[BotConfig.APPIUM_LOG_LEVEL])
                            //.withLogFile(new File("target/"+deviceUnderExecution+".log"))
                    )
                    appiumService!!.start()
                    runnerInstance!!.driverService = appiumService as Nothing?
                    isServiceRunning = true
                    return
                } else {
                    Platform.runLater { MessageDialog(0, "Could not find appium module at " + appiumPath.path + "!\nCheck your settings!", 750, 300) }
                    isServiceRunning = false
                    return
                }
            }
            else -> {
                LogFileHandler.logger.warning("Invalid bot type!")
                isServiceRunning = false
            }
        }
    }

    /**
     * Stops the driver service of the current botType
     */
    fun stopAppiumService() {
        if (isServiceRunning) {
            when (PluginConfig.botType) {
                BotType.Bot.CHROME -> chromeDriverService!!.stop()
                BotType.Bot.FIREFOX -> geckoDriverService!!.stop()
                BotType.Bot.ANDROID -> appiumService!!.stop()
            }
        }
    }

    companion object {
        var runnerInstance: BotRunner<*, *>? = null
            private set

        fun getCapabilities(plugin: PluginData): DesiredCapabilities? {
            val desiredCapabilities: DesiredCapabilities

            when (PluginConfig.botPlatform) {
                BotPlatforms.NONE -> {
                    LogFileHandler.logger.info("No platform selected!")
                    return null
                }

                BotPlatforms.WEB -> when (PluginConfig.botType) {
                    BotType.Bot.CHROME -> {
                        //set up chrome options
                        val options = ChromeOptions()
                        options.addArguments("user-data-dir=" + PluginConfig.chromeConfigDirectory)
                        options.addArguments("--profile-directory=" + PluginConfig.browserProfileName)
                        //                      options.setBinary(AppConfiguration.browserBinaryPath);

                        //set driver capabilities
                        desiredCapabilities = DesiredCapabilities.chrome()
                        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options)
                        return desiredCapabilities
                    }

                    BotType.Bot.FIREFOX -> {
                        desiredCapabilities = DesiredCapabilities.firefox()
                        desiredCapabilities.setCapability(FirefoxDriver.PROFILE, ProfilesIni().getProfile(PluginConfig.browserProfileName))
                        //                        desiredCapabilities.setCapability("marionette", true);
                        //                      desiredCapabilities.setCapability(FirefoxDriver.BINARY, AppConfiguration.browserBinaryPath);
                        return desiredCapabilities
                    }

                    else -> {
                        LogFileHandler.logger.info("Invalid browser! Check your browser selection!")
                        return null
                    }
                }

                BotPlatforms.ANDROID -> {
                    val capabilities = DesiredCapabilities()
                    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, BotConfig.DEVICE_NAME)
                    capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, BotConfig.VERSION)
                    capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "android")
                    capabilities.setCapability(MobileCapabilityType.UDID, BotConfig.UDID)
                    capabilities.setCapability(MobileCapabilityType.NO_RESET, true)
                    //                capabilities.setCapability(MobileCapabilityType.APP, BotConfig.APP_PATH);
                    capabilities.setCapability("appPackage", plugin.getPluginPackage())
                    capabilities.setCapability("appActivity", plugin.getPluginActvity())
                    return capabilities
                }

                else -> {
                    LogFileHandler.logger.info("Invalid platform! Check your platform setting!")
                    return null
                }
            }
        }
    }
}
