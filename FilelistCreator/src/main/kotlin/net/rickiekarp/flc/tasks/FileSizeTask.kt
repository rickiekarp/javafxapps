package net.rickiekarp.flc.tasks

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.flc.model.Filelist
import net.rickiekarp.flc.settings.AppConfiguration
import javafx.application.Platform
import javafx.concurrent.Task
import net.rickiekarp.flc.view.layout.MainLayout

import java.io.File

class FileSizeTask : Task<Void>() {

    @Throws(Exception::class)
    override fun call(): Void? {

        //calculate new file amount
        for (i in 0 until AppConfiguration.fileData.size) {
            val flist = AppConfiguration.fileData[i]
            val file = File(flist.getFilepath() + File.separator + AppConfiguration.fileData[i].getFilename())
            val fileSize = Filelist.calcFileSize(file)

            Platform.runLater { AppConfiguration.fileData.set(i, flist).setSize(fileSize) }
        }
        return null
    }

    init {

        this.setOnRunning { MainLayout.mainLayout.setStatus("neutral", LanguageController.getString("status_fileSizeUnitChange")) }

        this.setOnSucceeded {
            DebugHelper.profile("stop", "FileSizeTask")
            FilelistPreviewTask()
        }

        this.setOnFailed {
            DebugHelper.profile("stop", "FileSizeTask")
            MessageDialog(0, LanguageController.getString("unknownError"), 450, 220)
            LogFileHandler.logger.info("fileSizeTask.failed")
        }

        DebugHelper.profile("start", "FileSizeTask")

        //start the task in a new thread
        val sizeThread = Thread(this)
        sizeThread.isDaemon = true
        sizeThread.start()
    }
}
