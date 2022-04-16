package net.rickiekarp.flc.view.layout

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.ui.anim.AnimationHandler
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.core.view.layout.AppLayout
import net.rickiekarp.flc.controller.FilelistController
import net.rickiekarp.flc.model.Directorylist
import net.rickiekarp.flc.model.Filelist
import net.rickiekarp.flc.model.FilelistFormats
import net.rickiekarp.core.model.SettingEntry
import net.rickiekarp.flc.settings.AppConfiguration
import net.rickiekarp.flc.tasks.FilelistPreviewTask
import net.rickiekarp.flc.tasks.ListTask
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import net.rickiekarp.core.view.AboutScene
import net.rickiekarp.core.view.SettingsScene
import net.rickiekarp.flc.tasks.FileSizeTask

import java.text.DecimalFormatSymbols
import java.util.Collections
import net.rickiekarp.core.components.FoldableListCell

class MainLayout : AppLayout {
    override val layout: Node
        get() = createLayout()

    private var status: Label? = null
    private val fileListFormats = FXCollections.observableArrayList<FilelistFormats>()

    init {
        mainLayout = this
    }

    private fun createGenerat(description: String): VBox {
        val content = VBox()
        content.spacing = 5.0

        val btn_settings = Button()
        btn_settings.tooltip = Tooltip(LanguageController.getString("settings"))
        btn_settings.styleClass.add("decoration-button-settings")

        val btn_about = Button()
        btn_about.tooltip = Tooltip(LanguageController.getString("about"))
        btn_about.styleClass.add("decoration-button-about")

        btn_settings.setOnAction { event -> SettingsScene() }
        btn_about.setOnAction { event -> AboutScene() }

        val hbox = HBox()
        hbox.alignment = Pos.CENTER
        hbox.spacing = 5.0
        hbox.children.addAll(btn_settings, btn_about)

        content.children.addAll(hbox)
        return content
    }

    private fun createBox2(description: String): VBox {
        val content = VBox()
        content.spacing = 5.0

        val option2_desc = Label(LanguageController.getString(description))
        option2_desc.isWrapText = true
        option2_desc.style = "-fx-font-size: 9pt;"
        option2_desc.maxWidth = 175.0

        val filename = CheckBox(LanguageController.getString("name"))
        filename.isSelected = true
        FilelistController.option[0] = true
        filename.setOnAction { event ->
            FilelistController.option[0] = !FilelistController.option[0]
            LogFileHandler.logger.config("change_filename_option: " + !FilelistController.option[0] + " -> " + FilelistController.option[0])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val type = CheckBox(LanguageController.getString("ftype"))
        type.isSelected = false
        FilelistController.option[1] = false
        type.setOnAction { event ->
            FilelistController.option[1] = !FilelistController.option[1]
            LogFileHandler.logger.config("change_type_option: " + !FilelistController.option[1] + " -> " + FilelistController.option[1])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val path = CheckBox(LanguageController.getString("fpath"))
        path.isSelected = true
        FilelistController.option[2] = true
        path.setOnAction { event ->
            FilelistController.option[2] = !FilelistController.option[2]
            LogFileHandler.logger.config("change_path_option: " + !FilelistController.option[2] + " -> " + FilelistController.option[2])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val size = CheckBox(LanguageController.getString("fsize"))
        size.isSelected = true
        FilelistController.option[3] = true
        size.setOnAction { event ->
            FilelistController.option[3] = !FilelistController.option[3]
            LogFileHandler.logger.config("change_filesize_option: " + !FilelistController.option[3] + " -> " + FilelistController.option[3])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val created = CheckBox(LanguageController.getString("fcreation"))
        created.isSelected = false
        FilelistController.option[4] = false
        created.setOnAction { event ->
            FilelistController.option[4] = !FilelistController.option[4]
            LogFileHandler.logger.config("change_creationdate_option: " + !FilelistController.option[4] + " -> " + FilelistController.option[4])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val changed = CheckBox(LanguageController.getString("fmodif"))
        changed.isSelected = true
        FilelistController.option[5] = true
        changed.setOnAction { event ->
            FilelistController.option[5] = !FilelistController.option[5]
            LogFileHandler.logger.config("change_lastchanged_option: " + !FilelistController.option[5] + " -> " + FilelistController.option[5])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val lastAccess = CheckBox(LanguageController.getString("faccessed"))
        lastAccess.isSelected = false
        FilelistController.option[6] = false
        lastAccess.setOnAction { event ->
            FilelistController.option[6] = !FilelistController.option[6]
            LogFileHandler.logger.config("change_lastaccess_option: " + !FilelistController.option[6] + " -> " + FilelistController.option[6])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val hidden = CheckBox(LanguageController.getString("fhidden"))
        hidden.isSelected = false
        FilelistController.option[7] = false
        hidden.setOnAction { event ->
            FilelistController.option[7] = !FilelistController.option[7]
            LogFileHandler.logger.config("change_hidden: " + !FilelistController.option[7] + " -> " + FilelistController.option[7])
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        content.children.addAll(option2_desc, filename, type, path, size, created, changed, lastAccess, hidden)
        return content
    }

    private fun createBox1(description: String): VBox {
        val content = VBox()
        content.spacing = 5.0

        val option1_desc = Label(LanguageController.getString(description))
        option1_desc.isWrapText = true
        option1_desc.style = "-fx-font-size: 9pt;"
        option1_desc.maxWidth = 175.0

        val option = Label(LanguageController.getString("grouping"))
        val option1 = Label(LanguageController.getString("unit"))

        val cbox_sorting = ComboBox<String>()
        cbox_sorting.items.addAll(LanguageController.getString("none"), LanguageController.getString("folder"))
        cbox_sorting.selectionModel.select(0)
        cbox_sorting.minWidth = 100.0

        val cbox_unit = ComboBox<String>()
        cbox_unit.items.addAll(AppConfiguration.unitList)
        cbox_unit.selectionModel.select(FilelistController.UNIT_IDX)
        cbox_unit.minWidth = 100.0

        val header = CheckBox(LanguageController.getString("headerShow"))
        header.isSelected = FilelistController.canShowHeader

        val empty = CheckBox(LanguageController.getString("emptyFolderShow"))
        empty.isSelected = true

        cbox_sorting.valueProperty().addListener { ov, t, t1 ->
            FilelistController.sortingIdx = cbox_sorting.selectionModel.selectedIndex
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
            when (cbox_sorting.selectionModel.selectedIndex) {
                0 -> content.children.remove(empty)
                1 -> content.children.add(empty)
            }
        }

        cbox_unit.valueProperty().addListener { ov, t, t1 ->
            FilelistController.UNIT_IDX = cbox_unit.selectionModel.selectedIndex
            if (AppConfiguration.fileData.size > 0) {
                FileSizeTask()
            }
        }

        header.setOnAction { event ->
            FilelistController.canShowHeader = header.isSelected
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }

        }

        empty.setOnAction { event ->
            FilelistController.canShowEmptyFolder = empty.isSelected
            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
            }
        }

        val hbox = HBox()
        hbox.alignment = Pos.CENTER_LEFT
        hbox.spacing = 5.0
        hbox.children.addAll(cbox_sorting, option)

        val hbox1 = HBox()
        hbox1.alignment = Pos.CENTER_LEFT
        hbox1.spacing = 5.0
        hbox1.children.addAll(cbox_unit, option1)

        content.children.addAll(option1_desc, hbox, hbox1, header)
        return content
    }

    private fun createLayout(): Node {
        val mainContent = BorderPane()

        val fileGrid = GridPane()
        val columnConstraints = ColumnConstraints()
        columnConstraints.isFillWidth = true
        columnConstraints.hgrow = Priority.ALWAYS
        fileGrid.columnConstraints.add(columnConstraints)

        val controls = AnchorPane()
        controls.minHeight = 50.0

        fileControls = HBox(5.0)

        saveControls = HBox(8.0)
        saveControls.alignment = Pos.CENTER

        fileGrid.hgap = 15.0
        fileGrid.vgap = 5.0
        fileGrid.padding = Insets(5.0, 10.0, 5.0, 10.0)  //padding top, left, bottom, right

        //add components
        pathTF = TextField()
        pathTF.isEditable = false
        fileGrid.children.add(pathTF)

        val btn_browse = Button(LanguageController.getString("browse"))
        fileGrid.children.add(btn_browse)

        val subFolderBox = CheckBox(LanguageController.getString("inclSubdir")) //cb_subFolders.setSelected(subDirs);
        subFolderBox.isSelected = AppConfiguration.subFolderCheck
        fileGrid.children.add(subFolderBox)

        //FILE TABLE
        fileTable = TableView()
        //fileTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        fileTable.placeholder = Label(LanguageController.getString("no_file_found"))
        fileTable.setItems(null) //fixes NullPointerException when using arrow keys in empty table
        fileGrid.children.add(fileTable)


        val nameColumn = TableColumn<Filelist, String>(LanguageController.getString("name"))
        nameColumn.setCellValueFactory(PropertyValueFactory("filename"))
        nameColumn.prefWidth = 175.0
        fileColumn.add(nameColumn)

        val fTypeColumn = TableColumn<Filelist, String>(LanguageController.getString("ftype"))
        fTypeColumn.setCellValueFactory(PropertyValueFactory("filetype"))
        fTypeColumn.prefWidth = 100.0
        fileColumn.add(fTypeColumn)

        val fPathColumn = TableColumn<Filelist, String>(LanguageController.getString("fpath"))
        fPathColumn.setCellValueFactory(PropertyValueFactory("filepath"))
        fPathColumn.prefWidth = 180.0
        fileColumn.add(fPathColumn)

        val fSizeColumn = TableColumn<Filelist, String>(LanguageController.getString("fsize"))
        fSizeColumn.setCellValueFactory { p ->
            if (p.value != null) {
                SimpleStringProperty(p.value.getSize().toString() + " " + AppConfiguration.unitList[FilelistController.UNIT_IDX])
            } else {
                SimpleStringProperty("null")
            }
        }
        fSizeColumn.prefWidth = 75.0
        fileColumn.add(fSizeColumn)

        val fCreationColumn = TableColumn<Filelist, String>(LanguageController.getString("fcreation"))
        fCreationColumn.setCellValueFactory(PropertyValueFactory("creationDate"))
        fCreationColumn.prefWidth = 175.0
        fileColumn.add(fCreationColumn)

        val fmodifColumn = TableColumn<Filelist, String>(LanguageController.getString("fmodif"))
        fmodifColumn.setCellValueFactory(PropertyValueFactory("lastModif"))
        fmodifColumn.prefWidth = 175.0
        fileColumn.add(fmodifColumn)

        val faccessed = TableColumn<Filelist, String>(LanguageController.getString("faccessed"))
        faccessed.setCellValueFactory(PropertyValueFactory("lastAccessDate"))
        faccessed.prefWidth = 175.0
        fileColumn.add(faccessed)

        val fhidden = TableColumn<Filelist, String>(LanguageController.getString("fhidden")) //column8.setVisible(false);
        fhidden.setCellValueFactory(PropertyValueFactory("isHidden"))
        fileColumn.add(fhidden)

        fileTable.tableMenuButtonVisibleProperty().set(true)
        fileTable.columns.setAll(fileColumn)

        //DIRECTORY TABLE
        dirTable = TableView()
        dirTable.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        dirTable.placeholder = Label(LanguageController.getString("no_dir_found"))
        dirTable.setItems(null) //fixes NullPointerException when using arrow keys in empty table
        fileGrid.children.add(dirTable)


        val otherSymbols = DecimalFormatSymbols()
        otherSymbols.decimalSeparator = ','
        otherSymbols.groupingSeparator = '.'

        val tcolumn1 = TableColumn<Directorylist, String>(LanguageController.getString("name"))
        tcolumn1.setCellValueFactory(PropertyValueFactory("dir"))
        val tcolumn2 = TableColumn<Directorylist, Int>(LanguageController.getString("filesTotal"))
        tcolumn2.setCellValueFactory { cellData -> SimpleIntegerProperty(cellData.value.getFilesTotal()).asObject() }
        val tcolumn3 = TableColumn<Directorylist, Int>(LanguageController.getString("filesindir"))
        tcolumn3.setCellValueFactory { cellData -> SimpleIntegerProperty(cellData.value.filesInDir).asObject() }
        val tcolumn4 = TableColumn<Directorylist, Int>(LanguageController.getString("foldersInAmount"))
        tcolumn4.setCellValueFactory { cellData -> SimpleIntegerProperty(cellData.value.foldersInDir).asObject() }
        val tcolumn5 = TableColumn<Directorylist, String>(LanguageController.getString("fsize"))
        tcolumn5.setCellValueFactory { p ->
            if (p.value != null) {
                SimpleStringProperty(p.value.fileSizeInDir.toString() + " B")
            } else {
                SimpleStringProperty("null")
            }
        }

        dirTable.tableMenuButtonVisibleProperty().set(true)
        dirTable.columns.setAll(tcolumn1, tcolumn2, tcolumn3, tcolumn4, tcolumn5)

        previewTA = TextArea()
        previewTA.minHeight = 200.0
        fileGrid.children.add(previewTA)

        val btn_remove = Button(LanguageController.getString("removeFile"))

        btn_removeAll = Button(LanguageController.getString("removeAllFiles"))

        val fileunit = Label(LanguageController.getString("unit"))
        GridPane.setConstraints(fileunit, 0, 2)
        GridPane.setHalignment(fileunit, HPos.RIGHT)

        val settingsGrid = GridPane()
        settingsGrid.prefWidth = 200.0
        settingsGrid.vgap = 10.0
        settingsGrid.padding = Insets(5.0, 0.0, 0.0, 0.0)  //padding top, left, bottom, right
        settingsGrid.alignment = Pos.BASELINE_CENTER

        //SETTINGS LIST
        val list = ListView<SettingEntry>()
        GridPane.setConstraints(list, 0, 0)
        GridPane.setVgrow(list, Priority.ALWAYS)
        settingsGrid.children.add(list)

        val items = FXCollections.observableArrayList<SettingEntry>()
        if (Configuration.useSystemBorders) {
            items.add(SettingEntry("flSetting_0",false, createGenerat("flSetting_0_desc")))
        }
        items.add(SettingEntry("flSetting_1",false, createBox1("flSetting_1_desc")))
        items.add(SettingEntry("flSetting_2",false, createBox2("flSetting_2_desc")))
        list.items = items

        list.setCellFactory { FoldableListCell(list) }


        status = Label()
        saveControls.children.add(status)

        cbox_saveFormat = ComboBox()
        btn_saveFileList = Button(LanguageController.getString("saveList"))

        AnchorPane.setLeftAnchor(fileControls, 5.0)
        AnchorPane.setBottomAnchor(fileControls, 6.0)
        AnchorPane.setRightAnchor(saveControls, 5.0)
        AnchorPane.setBottomAnchor(saveControls, 6.0)

        controls.style = "-fx-background-color: #1d1d1d;"


        controls.children.add(fileControls)
        controls.children.add(saveControls)

        GridPane.setConstraints(fileGrid, 0, 0)
        GridPane.setConstraints(settingsGrid, 1, 0)

        GridPane.setConstraints(pathTF, 0, 0)
        GridPane.setConstraints(btn_browse, 1, 0)
        GridPane.setConstraints(subFolderBox, 2, 0)
        GridPane.setConstraints(fileTable, 0, 1)
        GridPane.setColumnSpan(fileTable, 3)
        GridPane.setConstraints(dirTable, 0, 2)
        GridPane.setColumnSpan(dirTable, 3)
        GridPane.setConstraints(previewTA, 0, 3)
        GridPane.setColumnSpan(previewTA, 3)


        //add components to borderpane
        mainContent.center = fileGrid
        mainContent.right = settingsGrid
        mainContent.bottom = controls

        btn_browse.setOnAction { event ->
            status!!.text = LanguageController.getString("files_loading")

            val directoryChooser = DirectoryChooser()
            val selectedDirectory = directoryChooser.showDialog(MainScene.mainScene.windowScene!!.window)

            if (selectedDirectory == null) {
                status!!.text = LanguageController.getString("no_dir_selected")
            } else {

                //clear all data
                if (AppConfiguration.fileData.size > 0 || AppConfiguration.dirData.size > 0) {
                    removeAllFiles()
                }

                pathTF.text = selectedDirectory.path

                //add start directory entry
                AppConfiguration.dirData.add(Directorylist(selectedDirectory.path, 0, 0, 0, 0))

                ListTask(selectedDirectory)
            }
        }

        //Add change listener
        fileTable.selectionModel.selectedItemProperty().addListener { observableValue, oldValue, newValue ->
            //Check whether item is selected
            if (fileTable.selectionModel.selectedItem != null) {

                //check if dirTable is selected
                if (dirTable.selectionModel.isSelected(dirTable.selectionModel.selectedIndex)) {
                    dirTable.selectionModel.select(null)
                }

                if (oldValue == null) {
                    fileControls.children.add(btn_remove)
                }
            } else {
                fileControls.children.remove(btn_remove)
                if (AppConfiguration.fileData.size == 0) {
                    fileTable.setItems(null)
                    AppConfiguration.dirData.clear()
                    dirTable.setItems(null)
                    ListTask.listTask.resetTask()
                    previewTA.clear()
                    pathTF.clear()
                    fileControls.children.removeAll(btn_removeAll)
                    saveControls.children.removeAll(cbox_saveFormat, btn_saveFileList)
                }
            }
        }

        fileTable.setOnKeyPressed { event ->
            if (event.code == KeyCode.DELETE) {
                if (!btn_remove.isDisabled) {
                    if (fileTable.selectionModel.isSelected(fileTable.selectionModel.selectedIndex)) {
                        removeFile()
                    }
                }
            }
        }

        dirTable.selectionModel.selectedItemProperty().addListener { observableValue, oldValue, newValue ->
            //Check whether item is selected
            if (dirTable.selectionModel.selectedItem != null) {

                //check if fileTable is selected
                if (fileTable.selectionModel.isSelected(fileTable.selectionModel.selectedIndex)) {
                    fileTable.selectionModel.select(null)
                }

                if (oldValue == null) {
                    fileControls.children.add(btn_remove)
                }
            } else {
                fileControls.children.remove(btn_remove)
                if (AppConfiguration.dirData.size == 0) {
                    dirTable.setItems(null)
                    ListTask.listTask.resetTask()
                    previewTA.clear()
                    pathTF.clear()
                    fileControls.children.removeAll(btn_removeAll)
                    saveControls.children.removeAll(cbox_saveFormat, btn_saveFileList)
                }
            }
        }

        dirTable.setOnKeyPressed { event ->
            if (event.code == KeyCode.DELETE) {
                if (!btn_remove.isDisabled) {
                    if (dirTable.selectionModel.isSelected(dirTable.selectionModel.selectedIndex)) {
                        removeFile()
                    }
                }
            }
        }

        btn_remove.setOnAction { event ->
            removeFile()
        }

        btn_removeAll.setOnAction { event -> removeAllFiles() }

        subFolderBox.setOnAction { event ->
            AppConfiguration.subFolderCheck = subFolderBox.isSelected
            if (subFolderBox.isSelected) {
                status!!.text = LanguageController.getString("incl_sub_on")
            } else {
                status!!.text = LanguageController.getString("incl_sub_off")
            }
        }

        btn_saveFileList.setOnAction { event ->
            val fileformatIDx = cbox_saveFormat.selectionModel.selectedIndex
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.addAll(
                    FileChooser.ExtensionFilter(
                            fileListFormats[fileformatIDx].fileTypeName,
                            fileListFormats[fileformatIDx].fileTypeEnding)
            )
            val file = fileChooser.showSaveDialog(MainScene.mainScene.windowScene!!.window)

            if (file != null) {
                FilelistController.flController!!.saveToFile(file, fileformatIDx)
                AnimationHandler.statusFade(status!!, "success", LanguageController.getString("save_filelist_success"))
                LogFileHandler.logger.info("save.filelist:Success")
            } else {
                AnimationHandler.statusFade(status!!, "neutral", LanguageController.getString("save_filelist_fail"))
                LogFileHandler.logger.info("save.filelist:Fail")
            }
        }

        //add all file formats to a list
        fillFileFormatList()

        //debug colors
        if (DebugHelper.isDebugVersion) {
            controls.style = "-fx-background-color: #336699;"
            fileGrid.style = "-fx-background-color: #555555"
            settingsGrid.style = "-fx-background-color: #444444"
            settingsGrid.isGridLinesVisible = true
            fileGrid.isGridLinesVisible = true
        } else {
            controls.style = "-fx-background-color: #1d1d1d;"
            fileGrid.style = null
            settingsGrid.style = null
            settingsGrid.isGridLinesVisible = false
            fileGrid.isGridLinesVisible = false
        }

        return mainContent
    }

    /**
     * Removes the selected index in the fileTable and calculates new file amount
     */
    private fun removeFile() {

        if (fileTable.selectionModel.isSelected(fileTable.selectionModel.selectedIndex)) {

            //calculate new file amount
            var dir: Directorylist
            for (i in 0 until AppConfiguration.dirData.size) {
                if (AppConfiguration.fileData[fileTable.selectionModel.selectedIndex].getFilepath() == AppConfiguration.dirData[i].getDir()) {
                    dir = AppConfiguration.dirData[i]
                    AppConfiguration.dirData.set(i, dir).setFilesFromDir(1)
                    AppConfiguration.dirData.set(i, dir).setFilesFromTotal(1)
                    AppConfiguration.dirData.set(i, dir).setFileSizeFromDir(AppConfiguration.fileData[fileTable.selectionModel.selectedIndex].getSize())
                }
            }

            //remove selected index
            AppConfiguration.fileData.removeAt(fileTable.selectionModel.selectedIndex)

            //if no files are in the filelist, remove the directory from the table
            //        if (AppConfiguration.dirData.get(selIdx).getFilesTotal() == 0) {
            //            for (int i = 0; i < AppConfiguration.dirData.size(); i++) {
            //                if (AppConfiguration.fileData.get(fileTable.getSelectionModel().getSelectedIndex()).getFilepath().equals(AppConfiguration.dirData.get(i).getDir())) {
            //                    AppConfiguration.dirData.remove(i);
            //                }
            //            }
            //        }

            if (AppConfiguration.fileData.size > 0) {
                FilelistPreviewTask()
                status!!.text = LanguageController.getString("remove_file_success")
            } else {
                status!!.text = LanguageController.getString("clear_filelist_success")
            }
        } else if (dirTable.selectionModel.isSelected(dirTable.selectionModel.selectedIndex)) {
            removeFolder()
            if (AppConfiguration.dirData.size > 0) {
                status!!.text = LanguageController.getString("remove_folder_success")
            } else {
                status!!.text = LanguageController.getString("clear_filelist_success")
            }
        }
    }

    /**
     * Removes the selected folder in the TableView
     */
    fun removeFolder() {

        val toDelList = FXCollections.observableArrayList<Int>()

        //iterate through filedata and add 'to delete items' to a list
        for (i in 0 until AppConfiguration.fileData.size) {
            if (AppConfiguration.fileData[i].getFilepath() == AppConfiguration.dirData[dirTable.selectionModel.selectedIndex].getDir()) {
                toDelList.add(i)
            }
        }

        //reverse sorting order of list
        Collections.reverse(toDelList)

        //delete items from list
        for (aList in toDelList) {
            val test = fileTable.items[aList!!]
            AppConfiguration.fileData.remove(test)
        }

        //remove directory entry from dirTable
        AppConfiguration.dirData.removeAt(dirTable.selectionModel.selectedIndex)

        if (AppConfiguration.fileData.size > 0) {
            FilelistPreviewTask()
        } else if (AppConfiguration.fileData.size == 0) {
            AppConfiguration.dirData.clear()
        }
    }

    /**
     * Removes all data in the TableView
     */
    fun removeAllFiles() {
        LogFileHandler.logger.info("Removing file data...")
        previewTA.clear()
        ListTask.listTask.deleteData()
        ListTask.listTask.resetTask()
        fileControls.children.remove(btn_removeAll)
        saveControls.children.removeAll(cbox_saveFormat, btn_saveFileList)
        setStatus("neutral", LanguageController.getString("clear_filelist_success"))
    }

    /**
     * Sets a status text in the MainScene
     */
    fun setStatus(type: String, msg: String) {
        AnimationHandler.statusFade(status!!, type, msg)
    }

    /**
     * Adds all FileList formats to a list
     */
    private fun fillFileFormatList() {
        fileListFormats.addAll(FilelistFormats("Text (.txt)", "*.txt"))
        fileListFormats.addAll(FilelistFormats("HTML (.html)", "*.html"))

        var i = 0
        while (fileListFormats.size > i) {
            cbox_saveFormat.items.addAll(fileListFormats[i].fileTypeName)
            i++
        }
        cbox_saveFormat.value = fileListFormats[0].fileTypeName
    }

    override fun postInit() {

    }

    companion object {
        lateinit var mainLayout: MainLayout
        lateinit var fileTable: TableView<Filelist>
        lateinit var dirTable: TableView<Directorylist>
        lateinit var btn_removeAll: Button
        lateinit var btn_saveFileList: Button
        lateinit var pathTF: TextField
        lateinit var previewTA: TextArea
        lateinit var cbox_saveFormat: ComboBox<String>
        lateinit var fileControls: HBox
        lateinit var saveControls: HBox
        var fileColumn: ArrayList<TableColumn<Filelist, String>?> = arrayListOf()
    }
}
