package net.rickiekarp.flc.model

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.flc.controller.FilelistController
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty

import java.io.File

/**
 * This is the model class for all files of the filelist.
 */
class Filelist(aName: String, aType: String, aPath: String, aSize: Long, aCreationTime: String, aMoodifDate: String, aAccessDate: String, hidden: Boolean) {
    private val filename: SimpleStringProperty = SimpleStringProperty(aName)
    private val filetype: SimpleStringProperty = SimpleStringProperty(aType)
    private val filepath: SimpleStringProperty = SimpleStringProperty(aPath)
    private val size: SimpleLongProperty = SimpleLongProperty(aSize)
    private val creationDate: SimpleStringProperty = SimpleStringProperty(aCreationTime)
    private val moodifDate: SimpleStringProperty = SimpleStringProperty(aMoodifDate)
    private val lastAccessDate: SimpleStringProperty = SimpleStringProperty(aAccessDate)
    private val isHidden: SimpleStringProperty

    var lastModif: String
        get() = moodifDate.get()
        set(aFile5) = moodifDate.set(aFile5)

    init {
        if (hidden) {
            this.isHidden = SimpleStringProperty(LanguageController.getString("yes"))
        } else {
            this.isHidden = SimpleStringProperty(LanguageController.getString("no"))
        }
    }

    fun getFilename(): String {
        return filename.get()
    }

    fun setFilename(aFile1: String) {
        filename.set(aFile1)
    }

    fun getFiletype(): String {
        return filetype.get()
    }

    fun setFiletype(aFile2: String) {
        filetype.set(aFile2)
    }

    fun getFilepath(): String {
        return filepath.get()
    }

    fun setFilepath(aFile3: String) {
        filepath.set(aFile3)
    }

    fun getSize(): Long {
        return size.get()
    }

    fun setSize(aSize: Long) {
        size.set(aSize)
    }

    fun getCreationDate(): String {
        return creationDate.get()
    }

    fun setCreationDate(aCreationDate: String) {
        creationDate.set(aCreationDate)
    }

    fun getLastAccessDate(): String {
        return lastAccessDate.get()
    }

    fun setLastAccessDate(aAccessDate: String) {
        lastAccessDate.set(aAccessDate)
    }

    fun getIsHidden(): String {
        return isHidden.get()
    }

    fun setIsHidden(hidden: String) {
        isHidden.set(hidden)
    }

    companion object {
        /**
         * Returns the filesize in a certain format
         */
        fun calcFileSize(file: File): Long {
            when (FilelistController.UNIT_IDX) {
                0 -> return file.length()
                1 -> return if (file.length() > 0 && file.length() < 1024) {
                    1
                } else {
                    file.length() / 1024
                }
                2 -> return file.length() / 1024 / 1024
                3 -> return file.length() / 1024 / 1024 / 1024
                4 -> return file.length() / 1024 / 1024 / 1024 / 1024
            }
            return -1
        }

        /**
         * Returns the file extension of a given fileName
         */
        fun getExtension(fileName: String): String {
            var extension = ""
            val i = fileName.lastIndexOf('.')
            if (i > 0) {
                extension = fileName.substring(i + 1).toUpperCase()
            }
            return extension
        }
    }
}
