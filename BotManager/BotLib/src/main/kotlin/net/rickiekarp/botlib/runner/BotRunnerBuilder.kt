package net.rickiekarp.botlib.runner

import net.rickiekarp.core.view.MainScene
import net.rickiekarp.botlib.BotLauncher
import net.rickiekarp.botlib.PluginConfig
import net.rickiekarp.botlib.model.PluginData
import net.rickiekarp.botlib.plugin.BotSetting
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.service.local.AppiumDriverLocalService
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import net.rickiekarp.botlib.enums.BotPlatforms
import net.rickiekarp.botlib.enums.BotType
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.service.DriverService

class BotRunnerBuilder {
    fun build(plugin: PluginData): BotRunner<*, *>? {
        when (PluginConfig.botPlatform) {
            BotPlatforms.WEB -> return object : BotRunner<RemoteWebDriver, DriverService>() {
                override fun start() {
                    when (PluginConfig.botType) {
                        //                            case FIREFOX: set(new MarionetteDriver((GeckoDriverService) getDriverService(), BotLauncher.getCapabilities(plugin))); break;
                        BotType.Bot.CHROME -> set(ChromeDriver(driverService as ChromeDriverService, BotLauncher.getCapabilities(plugin)))
                    }
                }

                override fun setLayout(node: Node) {
                    Platform.runLater { MainScene.mainScene.borderPane.center = node }
                }

                override fun addSetting(title: String, description: String, isVisible: Boolean, settingNode: Node) {
                    Platform.runLater {
                        PluginConfig.settingsList!!.add(
                                BotSetting.Builder.create().setName(title).setDescription(description).setVisible(isVisible).setNode(settingNode).build()
                        )
                    }
                }

                override fun addControlButton(vararg node: Button) {
                    Platform.runLater {
                        val pane = MainScene.mainScene.borderPane.bottom as AnchorPane
                        val controls = pane.children[0] as HBox
                        controls.children.addAll(*node)
                    }
                }

                override fun removeControlButton(index: Int) {
                    Platform.runLater {
                        val pane = MainScene.mainScene.borderPane.bottom as AnchorPane
                        val controls = pane.children[0] as HBox
                        controls.children.removeAt(index)
                    }
                }
            }

            BotPlatforms.ANDROID -> return object : BotRunner<AndroidDriver<*>, DriverService>() {
                override fun start() {
                    set(AndroidDriver<WebElement>(driverService as AppiumDriverLocalService, BotLauncher.getCapabilities(plugin)))
                }

                override fun setLayout(node: Node) {
                    Platform.runLater { MainScene.mainScene.borderPane.center = node }
                }

                override fun addSetting(title: String, description: String, isVisible: Boolean, settingNode: Node) {
                    Platform.runLater {
                        PluginConfig.settingsList!!.add(
                                BotSetting.Builder.create().setName(title).setDescription(description).setVisible(isVisible).setNode(settingNode).build()
                        )
                    }
                }

                override fun addControlButton(vararg node: Button) {
                    Platform.runLater {
                        val pane = MainScene.mainScene.borderPane.bottom as AnchorPane
                        val controls = pane.children[0] as HBox
                        controls.children.addAll(*node)
                    }
                }

                override fun removeControlButton(index: Int) {
                    Platform.runLater {
                        val pane = MainScene.mainScene.borderPane.bottom as AnchorPane
                        val controls = pane.children[0] as HBox
                        controls.children.removeAt(index)
                    }
                }
            }

            else -> {
                println("no browser selected")
                return null
            }
        }
    }
}