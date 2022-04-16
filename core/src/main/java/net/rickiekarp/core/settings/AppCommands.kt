package net.rickiekarp.core.settings

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.model.ConsoleCommands
import net.rickiekarp.core.view.CommandsScene
import net.rickiekarp.core.view.MessageDialog
import javafx.collections.FXCollections

import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.URISyntaxException

class AppCommands {

    /**
     * Console commands are added to a list.
     */
    @Throws(NoSuchMethodException::class)
    fun fillCommandsList() {
        commandsList.add(ConsoleCommands("/help", "", LanguageController.getString("commandsList_desc")) {
            help()
        })
        commandsList.add(ConsoleCommands("/exceptionTest", "", LanguageController.getString("throwsException")) {
            exceptionTest()
        })
        commandsList.add(ConsoleCommands("/errorTest", "", LanguageController.getString("showTest_desc")) {
            errorTest()
        })
        commandsList.add(ConsoleCommands("/restart", "", LanguageController.getString("restart_desc")) {
            restart()
        })
    }

    fun help() {
        val commands = CommandsScene()
        if (commands.commandsWindow!!.win.windowStage.stage.isShowing) {
            commands.commandsWindow!!.win.windowStage.stage.requestFocus()
        } else {
            commands.commandsWindow!!.win.windowStage.stage.show()
        }
    }

    fun exceptionTest() {
        ExceptionHandler.throwTestException()
    }

    fun errorTest() {
        MessageDialog(0, "TEST", 450, 220)
    }

    fun restart() {
        try {
            DebugHelper.restartApplication()
        } catch (e1: URISyntaxException) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        } catch (e1: IOException) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }
    }

    companion object {
        var commandsList = FXCollections.observableArrayList<ConsoleCommands>()

        /**
         * Checks and executed the entered command.
         * @param command Entered command
         */
        fun execCommand(command: String) {
            if (AppCommands.commandsList.isEmpty()) {
                println("No commands found!")
                return
            }

            //trim spaces of command string
            val finalCommand = command.replace("\\s+".toRegex(), " ")

            for (i in commandsList.indices) {
                if (finalCommand.startsWith(commandsList[i].commandName)) {
                    val methodName = commandsList[i].commandName.substring(1, commandsList[i].commandName.length) // methodname to be invoked

                    if (finalCommand == commandsList[i].commandName || finalCommand == commandsList[i].commandName + " ") {
                        val myMethod: Method
                        try {
                            commandsList[i].method()
                        } catch (e: InvocationTargetException) {
                            e.printStackTrace()
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: NoSuchMethodException) {
                            LogFileHandler.logger.warning("Method not found!")
                        }
                    }
                }
            }
        }
    }
}
