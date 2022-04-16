package net.rickiekarp.flc.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty

/**
 * This is the model class for all sub-directories of the filelist.
 * dir: Path of the sub-directory
 * filesindir: How many files are in the directory
 */
class Directorylist(directory: String, filestotal: Int, fileAmount: Int, folderAmount: Int, fileSize: Long) {
    private val dir: SimpleStringProperty = SimpleStringProperty(directory)
    private val filesTotal: SimpleIntegerProperty = SimpleIntegerProperty(filestotal)
    private val filesindir: SimpleIntegerProperty = SimpleIntegerProperty(fileAmount)
    private val foldersindir: SimpleIntegerProperty = SimpleIntegerProperty(folderAmount)
    private val filesizeindir: SimpleLongProperty = SimpleLongProperty(fileSize)

    var filesInDir: Int
        get() = filesindir.get()
        set(amount) = filesindir.set(filesInDir + amount)

    var foldersInDir: Int
        get() = foldersindir.get()
        set(amount) = foldersindir.set(foldersInDir + amount)

    var fileSizeInDir: Long
        get() = filesizeindir.get()
        set(size) = filesizeindir.set(fileSizeInDir + size)

    fun getDir(): String {
        return dir.get()
    }

    fun setDir(aFile1: String) {
        dir.set(aFile1)
    }

    fun getFilesTotal(): Int {
        return filesTotal.get()
    }

    fun setFilesTotal(amount: Int) {
        filesTotal.set(getFilesTotal() + amount)
    }

    fun setFilesFromTotal(amount: Int) {
        filesTotal.set(getFilesTotal() - amount)
    }

    fun setFilesFromDir(amount: Int) {
        filesindir.set(filesInDir - amount)
    }

    fun setFoldersFromDir(amount: Int) {
        foldersindir.set(foldersInDir - amount)
    }

    fun setFileSizeFromDir(size: Long) {
        filesizeindir.set(fileSizeInDir - size)
    }
}
