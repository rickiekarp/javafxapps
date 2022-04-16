package net.rickiekarp.qaacc.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Projects(aProjectID: Int, aProject: String, aXML: String, aAttr: String, accBMIdx: Int, accBMName: String) {
    private val projectID: SimpleIntegerProperty = SimpleIntegerProperty(aProjectID)
    private val projectName: SimpleStringProperty = SimpleStringProperty(aProject)
    private val projectXML: SimpleStringProperty = SimpleStringProperty(aXML)
    private val projectAttribute: SimpleStringProperty = SimpleStringProperty(aAttr)
    private val projectAccBookmarkIdx: SimpleIntegerProperty = SimpleIntegerProperty(accBMIdx)
    private val projectAccBookmarkName: SimpleStringProperty = SimpleStringProperty(accBMName)

    fun getProjectID(): Int {
        return projectID.get()
    }

    fun getProjectName(): String {
        return projectName.get()
    }

    fun getProjectXML(): String {
        return projectXML.get()
    }

    fun getProjectAttribute(): String {
        return projectAttribute.get()
    }

    fun getProjectAccBookmarkIdx(): Int {
        return projectAccBookmarkIdx.get()
    }

    fun setProjectAccBookmarkIdx(aBMID: Int) {
        projectAccBookmarkIdx.set(aBMID)
    }

    fun getProjectAccBookmarkName(): String {
        return projectAccBookmarkName.get()
    }

    fun setProjectAccBookmarkName(aBMName: String) {
        projectAccBookmarkName.set(aBMName)
    }
}
