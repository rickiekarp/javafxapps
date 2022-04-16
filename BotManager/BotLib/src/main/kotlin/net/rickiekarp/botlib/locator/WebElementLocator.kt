package net.rickiekarp.botlib.locator

import net.rickiekarp.botlib.BotLauncher
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

internal class WebElementLocator {
    private val driver: WebDriver

    init {
        driver = BotLauncher.runnerInstance!!.get() as WebDriver
    }

    fun getElement(byString: By): WebElement? {
        try {
            return driver.findElement(byString)
        } catch (e: NoSuchElementException) {
            return null
        }

    }

}
