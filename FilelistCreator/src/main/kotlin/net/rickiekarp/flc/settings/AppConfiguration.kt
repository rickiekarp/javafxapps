package net.rickiekarp.flc.settings

import net.rickiekarp.core.settings.LoadSave
import net.rickiekarp.flc.model.Directorylist
import net.rickiekarp.flc.model.Filelist
import javafx.collections.FXCollections

class AppConfiguration {

    companion object {
        /** Observable lists  */
        var fileData = FXCollections.observableArrayList<Filelist>()
        var dirData = FXCollections.observableArrayList<Directorylist>()
        var unitList = FXCollections.observableArrayList("B", "KB", "MB", "GB", "TB")

        /** settings  */
        @LoadSave
        var subFolderCheck: Boolean = true
    }
}
