package net.rickiekarp.core.model

import javafx.beans.property.SimpleStringProperty

/**
 * Created by rickie on 3/12/2016.
 */
class ChangelogEntry(version: String, date: String, desc: String) {
    private val version: SimpleStringProperty = SimpleStringProperty(version)
    private val date: SimpleStringProperty = SimpleStringProperty(date)
    private val desc: SimpleStringProperty = SimpleStringProperty(desc)

    fun getVersion(): String {
        return version.get()
    }

    fun getDate(): String {
        return date.get()
    }

    fun getDesc(): String {
        return desc.get()
    }

}
