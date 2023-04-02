package net.rickiekarp.flc.settings

import javafx.collections.FXCollections
import net.rickiekarp.core.settings.LoadSave
import net.rickiekarp.flc.model.Directorylist
import net.rickiekarp.flc.model.Filelist

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
