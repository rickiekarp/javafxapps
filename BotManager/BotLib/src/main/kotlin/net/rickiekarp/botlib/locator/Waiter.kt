package net.rickiekarp.botlib.locator

import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.botlib.BotLauncher
import net.rickiekarp.botlib.PluginConfig
import io.appium.java_client.android.AndroidDriver
import net.rickiekarp.botlib.enums.BotType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
//import org.openqa.selenium.firefox.MarionetteDriver;

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Waiter {

    @Synchronized
    fun waitForElement(timeOutInSeconds: Int, byString: By): WebElement? {
        val elem = arrayOfNulls<WebElement>(1)
        val locator = WebElementLocator()

        val scheduler = Executors.newScheduledThreadPool(1)

        val beeper = {
            elem[0] = locator.getElement(byString)

            if (elem[0] != null) {
                scheduler.shutdownNow()
            }
        }
        val beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 1, TimeUnit.SECONDS)
        scheduler.schedule({
            beeperHandle.cancel(true)
            scheduler.shutdown()
        }, timeOutInSeconds.toLong(), TimeUnit.SECONDS)

        try {
            scheduler.awaitTermination(timeOutInSeconds.toLong(), TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return elem[0]
    }

    /**
     * Tries to find the element of a given By String for a given time duration and returns it
     * @param timeout Duration to check for the given element
     * @param byString byString to search for
     * @return Element of the given byString
     */
    fun waitForElementToAppear(timeout: Long, byString: By): WebElement? {
        val currentTime = System.currentTimeMillis() / 1000
        val endTime = currentTime + timeout
        var element: WebElement? = null

        var whetherTimeout: Boolean
        while (true) {
            whetherTimeout = System.currentTimeMillis() / 1000 > endTime
            if (whetherTimeout) {
                LogFileHandler.logger.warning("no element found by $byString")
                break
            } else {
                try {
                    when (PluginConfig.botType) {
                        BotType.Bot.ANDROID -> {
                            val androidDriver = BotLauncher.runnerInstance!!.get() as AndroidDriver<*>
                            element = androidDriver.findElement(byString)
                        }
                        BotType.Bot.FIREFOX -> {
                        }
                        BotType.Bot.CHROME -> {
                            val chromeDriver = BotLauncher.runnerInstance!!.get() as ChromeDriver
                            element = chromeDriver.findElement(byString)
                        }
                        else -> throw RuntimeException("Invalid bot type! Check your settings!")
                    }//                            MarionetteDriver firefoxDriver = (MarionetteDriver) BotLauncher.getRunnerInstance().get();
                    //                            element = firefoxDriver.findElement(byString);
                } catch (e: Exception) {
                    //ignore
                }

                if (element != null) {
                    break
                }
            }

            //prevent spamming of server requests
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
        return element
    }
}
