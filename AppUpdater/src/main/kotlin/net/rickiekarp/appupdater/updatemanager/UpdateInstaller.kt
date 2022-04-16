package net.rickiekarp.appupdater.updatemanager

import net.rickiekarp.appupdater.UpdateMain
import net.rickiekarp.appupdater.ui.UpdateCheckerGUI

import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.ArrayList

class UpdateInstaller {

    private var jarFile: File? = null

    init {
        try {
            jarFile = File(UpdateMain::class.java.protectionDomain.codeSource.location.toURI().path)
        } catch (e: URISyntaxException) {
            UpdateCheckerGUI.updateChecker!!.appendMessage(getExceptionString(e))
            e.printStackTrace()
        }

    }

    fun installFiles() {
        //update location
        val file = File(jarFile!!.parent + File.separator + "data/update/")

        if (file.exists()) {
            //list all files in directory
            val listOfFiles = file.listFiles()

            if (listOfFiles!!.size > 0) {
                val copyTo = File(jarFile!!.parent).toPath()

                //iterate through directory
                for (afile in listOfFiles) {
                    val copyFrom = afile.toPath()

                    try {
                        Files.copy(copyFrom, copyTo.resolve(copyFrom.fileName), StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        UpdateCheckerGUI.updateChecker!!.appendMessage(getExceptionString(e))
                    }

                }
                cleanup(listOfFiles)
            } else {
                val updateChecker = UpdateCheckerGUI()
                updateChecker.message = "Can not find any files to update!"
                try {
                    launchProgram()
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                    UpdateCheckerGUI.updateChecker!!.appendMessage(getExceptionString(e))
                } catch (e: IOException) {
                    e.printStackTrace()
                    UpdateCheckerGUI.updateChecker!!.appendMessage(getExceptionString(e))
                }

            }
        } else {
            val updateChecker = UpdateCheckerGUI()
            updateChecker.message = "Can not find update directory " + file.path
        }
    }

    private fun cleanup(files: Array<File>) {
        println("Preforming clean up...")

        //iterate through start directory
        for (afile in files) {
            println("deleting: $afile")

            afile.delete()
        }

        println("Cleaned up...")
        println("Update done!")

        try {
            launchProgram()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            UpdateCheckerGUI.updateChecker!!.appendMessage(getExceptionString(e))
        } catch (e: IOException) {
            e.printStackTrace()
            UpdateCheckerGUI.updateChecker!!.appendMessage(getExceptionString(e))
        }

    }

    @Throws(URISyntaxException::class, IOException::class)
    private fun launchProgram() {
        val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
        val currentJar = File(UpdateMain::class.java.protectionDomain.codeSource.location.toURI())

        /* Build command: java -jar application.jar */
        val command = ArrayList<String>()
        command.add(javaBin)
        command.add("-jar")
        command.add(currentJar.parent + File.separator + UpdateMain.getArgs()[1])

        val builder = ProcessBuilder(command)
        builder.start()
        System.exit(0)
    }

    /**
     * Returns exception string
     */
    fun getExceptionString(t: Throwable): String {
        val sb = StringBuilder()

        sb.append(t.toString()).append(System.getProperty("line.separator"))

        val trace = t.stackTrace
        for (aTrace in trace) {
            sb.append("       at ").append(aTrace.toString()).append(System.getProperty("line.separator"))
        }
        return sb.toString()
    }
}
