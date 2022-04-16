package net.rickiekarp.flc.tasks

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.util.FileUtil
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.flc.controller.FilelistController
import net.rickiekarp.flc.model.Directorylist
import net.rickiekarp.flc.model.Filelist
import net.rickiekarp.flc.settings.AppConfiguration
import net.rickiekarp.flc.view.dialogs.ProgressDialog
import javafx.concurrent.Task
import net.rickiekarp.flc.view.layout.MainLayout

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.Date

class ListTask(private var selectedDirectory: File?) : Task<Void>() {
    private var listIdx = 0

    @Throws(Exception::class)
    override fun call(): Void? {

        //list all files in start directory
        val listOfFiles = FileUtil.getListOfFiles(selectedDirectory!!)

        if (listOfFiles != null) {

            //iterate through start directory
            for (file in listOfFiles) {

                //is it possible to read the file/folder?
                if (file.canRead()) {

                    //is it a file?
                    if (file.isFile) {

                        AppConfiguration.dirData[listIdx].filesInDir = 1 //increase file count
                        AppConfiguration.dirData[listIdx].fileSizeInDir = file.length()

                        val attrs = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)

                        AppConfiguration.fileData.add(Filelist(
                                file.name,
                                Filelist.getExtension(file.name),
                                file.parentFile.path,
                                Filelist.calcFileSize(file),
                                SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(attrs.creationTime().toMillis()),
                                SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(Date(file.lastModified())),
                                SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(attrs.lastAccessTime().toMillis()),
                                file.isHidden
                        )
                        )
                    } else if (file.isDirectory) {
                        AppConfiguration.dirData[listIdx].foldersInDir = 1 //increase foler count
                        AppConfiguration.dirData.add(Directorylist(file.path, 0, 0, 0, 0)) //add new directory
                    }//is it a folder?
                } else {
                    LogFileHandler.logger.warning("Couldn't read files in directory: " + file.path)
                }
            }
        }

        val filesTotal = AppConfiguration.dirData[listIdx].filesInDir + AppConfiguration.dirData[listIdx].foldersInDir
        AppConfiguration.dirData[listIdx].setFilesTotal(filesTotal)

        if (AppConfiguration.subFolderCheck) {
            listIdx++
            if (listIdx < AppConfiguration.dirData.size) {
                selectedDirectory = File(AppConfiguration.dirData[listIdx].getDir())
                call()
            }
        }
        return null
    }

    init {

        //create ProgressDialog
        val progressDialog = ProgressDialog()

        this.setOnSucceeded {
            DebugHelper.profile("stop", "ListTask")

            //set items to fileTable
            MainLayout.fileTable.items = AppConfiguration.fileData
            MainLayout.dirTable.items = AppConfiguration.dirData
            MainLayout.fileControls.children.add(MainLayout.btn_removeAll)
            MainLayout.saveControls.children.addAll(MainLayout.cbox_saveFormat, MainLayout.btn_saveFileList)

            //calculate text length for TextArea
            FilelistController.flController!!.calcColumnLenght()

            FilelistPreviewTask()

            progressDialog.close()
            LogFileHandler.logger.info("close.progressDialog")

            MainLayout.mainLayout.setStatus("neutral", LanguageController.getString("ready"))

            LogFileHandler.logger.info("fileScan.COMPLETE - " + AppConfiguration.fileData.size + " files / " + AppConfiguration.dirData.size + " folders scanned!")

        }

        this.setOnCancelled {
            DebugHelper.profile("stop", "ListTask")

            //delete already scanned data
            deleteData()

            //make sure to set listIdx to 0 when cancelling the task
            //fixes a bug where listIdx > 0 when starting the task after cancelling
            resetTask()

            progressDialog.close()
            LogFileHandler.logger.info("close.progressDialog")

            MainLayout.mainLayout.setStatus("neutral", LanguageController.getString("filescan_cancelled"))
            LogFileHandler.logger.info("fileScan.cancelled")
        }

        this.setOnFailed {
            DebugHelper.profile("stop", "ListTask")
            progressDialog.close()
            MessageDialog(0, LanguageController.getString("unknownError"), 450, 220)
            LogFileHandler.logger.info("fileScan.failed")
        }

        listTask = this
        FilelistController.flController = FilelistController()

        DebugHelper.profile("start", "ListTask")

        //start the task in a new thread
        val listThread = Thread(listTask)
        listThread.isDaemon = true
        listThread.start()
    }//set variables for list task

    fun resetTask() {
        listIdx = 0
    }

    fun deleteData() {
        //delete already scanned data
        AppConfiguration.fileData.clear()
        AppConfiguration.dirData.clear()
        MainLayout.fileTable.items = null
        MainLayout.dirTable.items = null
    }

    companion object {
        lateinit var listTask: ListTask
    }
}
