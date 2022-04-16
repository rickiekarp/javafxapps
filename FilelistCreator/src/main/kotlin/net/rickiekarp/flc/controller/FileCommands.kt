package net.rickiekarp.flc.controller

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.model.ConsoleCommands
import net.rickiekarp.core.settings.AppCommands
import net.rickiekarp.core.util.CommonUtil
import net.rickiekarp.flc.view.layout.MainLayout

/**
 * This class is used to define additional application related console commands.
 * Simply add the new command to the commands list and define the method to execute.
 */
class FileCommands {
    @Throws(NoSuchMethodException::class)
    fun addFileCommands() {
        AppCommands.commandsList.add(ConsoleCommands("/selectRandomFile", "", LanguageController.getString("desc_file_select_random")) {
            selectRandomFile()
        })
    }

    companion object {
        @JvmStatic
        fun selectRandomFile() {
            if (MainLayout.fileTable.items != null && MainLayout.fileTable.items.size > 0) {
                val randomNumber = CommonUtil.randInt(0, MainLayout.fileTable.items.size - 1)
                MainLayout.fileTable.selectionModel.select(randomNumber)
                MainLayout.mainLayout.setStatus("neutral", MainLayout.fileTable.items[randomNumber].getFilename() + " selected!")
            } else {
                MainLayout.mainLayout.setStatus("neutral", "No file found!")
            }
        }
    }
}
