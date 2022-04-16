package net.rickiekarp.flc.model

import javafx.beans.property.SimpleStringProperty

/**
 * This is the model class for all formats of the FileList (.txt / .html etc.)
 */
class FilelistFormats(aFile1: String, aFile2: String) {
    private val filetypeName: SimpleStringProperty = SimpleStringProperty(aFile1)
    private val filetypeEnding: SimpleStringProperty = SimpleStringProperty(aFile2)

    val fileTypeName: String
        get() = filetypeName.get()

    val fileTypeEnding: String
        get() = filetypeEnding.get()

    fun setFiletypeName(aFile1: String) {
        filetypeName.set(aFile1)
    }

    fun setFiletypeEnding(aFile2: String) {
        filetypeEnding.set(aFile2)
    }
}
