package net.rickiekarp.botter.view

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.net.NetResponse
import net.rickiekarp.core.net.update.FileDownloader
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.util.FileUtil
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.botlib.enums.BotPlatforms
import net.rickiekarp.botlib.model.PluginData
import net.rickiekarp.botlib.net.BotNetworkApi
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import net.rickiekarp.botter.settings.AppConfiguration
import net.rickiekarp.core.ui.tray.ToolTrayIcon
import org.json.JSONArray
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class PluginManagerLayout {
    private var pluginTable: TableView<PluginData>? = null
    private var status: Label? = null
    private var progressIndicator: ProgressIndicator? = null


    private//padding top, left, bottom, right
    //add vbox & controls pane to borderpane layout
    val content: BorderPane
        get() {
            val borderpane = BorderPane()
            borderpane.style = "-fx-background-color: #1d1d1d;"

            val controls = HBox()

            val statusBox = HBox(10.0)
            statusBox.alignment = Pos.BOTTOM_RIGHT
            status = Label()
            progressIndicator = ProgressIndicator()
            progressIndicator!!.maxHeight = 30.0
            progressIndicator!!.maxWidth = 30.0
            progressIndicator!!.progress = ProgressIndicator.INDETERMINATE_PROGRESS
            progressIndicator!!.isVisible = false
            statusBox.children.addAll(progressIndicator, status)


            pluginTable = TableView()
            pluginTable!!.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            pluginTable!!.placeholder = Label(LanguageController.getString("no_plugin_found"))
            GridPane.setConstraints(pluginTable, 0, 1)
            GridPane.setHgrow(pluginTable, Priority.ALWAYS)

            pluginTable!!.fixedCellSize = 40.0
            pluginTable!!.items = PluginData.pluginData

            controls.children.addAll(statusBox)

            controls.padding = Insets(10.0, 7.0, 10.0, 7.0)
            controls.spacing = 10.0
            controls.alignment = Pos.CENTER_RIGHT

            if (DebugHelper.isDebugVersion) {
                controls.style = "-fx-background-color: #336699;"
            } else {
                controls.style = null
            }
            borderpane.center = pluginTable
            borderpane.bottom = controls

            return borderpane
        }

    init {
        create()
        setupTable()
        loadRemotePlugins()
    }

    private fun loadRemotePlugins() {
        setLoadingBar(LanguageController.getString("loading"), true)

        Thread {
            //list all plugins where a new version needs to be fetched
            val toFetchVersion = ArrayList<PluginData>()
            for (i in 0 until PluginData.pluginData.size) {
                if (PluginData.pluginData[i].getPluginNewVersion() == null) {
                    toFetchVersion.add(PluginData.pluginData[i])
                    PluginData.pluginData[i].setPluginNewVersion(LanguageController.getString("fetching"))
                }
            }

            if (toFetchVersion.size > 0 || PluginData.pluginData.size == 0) {
                pluginTable!!.refresh()
                val response = NetResponse.getResponseString(AppContext.context.networkApi.runNetworkAction(BotNetworkApi.requestPlugins()))

                when (response) {
                    "no_connection", "file_not_found" -> {
                        Platform.runLater { setLoadingBar(LanguageController.getString("no_connection"), false) }
                    }
                    else -> {
                        val pluginArray = JSONArray(response)
                        var pluginEntry: JSONObject
                        entry@ for (i in 0 until pluginArray.length()) {
                            pluginEntry = pluginArray.get(i) as JSONObject
                            LogFileHandler.logger.info("Remote Plugin: " + pluginEntry.getString("identifier") + " - " + pluginEntry.getString("version"))
                            for (localPlugin in 0 until PluginData.pluginData.size) {
                                if (pluginEntry.getString("identifier") == PluginData.pluginData[localPlugin].pluginName.get()) {
                                    PluginData.pluginData[i].setPluginNewVersion(pluginEntry.getString("version"))
                                    PluginData.pluginData[i].updateEnable = pluginEntry.getBoolean("updateEnable")
                                    continue@entry
                                }
                            }

                            //if no corresponding plugin entry is found, add new entry
                            val pluginData = PluginData(null,
                                    pluginEntry.getString("identifier"), null,
                                    pluginEntry.getString("version"),
                                    BotPlatforms.valueOf(pluginEntry.getString("type"))
                            )
                            pluginData.updateEnable = pluginEntry.getBoolean("updateEnable")
                            PluginData.pluginData.add(pluginData)
                        }
                        pluginTable!!.refresh()
                    }
                }
            }

            //set version to 'no version found' for plugins without remote version
            for (i in 0 until PluginData.pluginData.size) {
                if (PluginData.pluginData[i].getPluginNewVersion() == LanguageController.getString("fetching")) {
                    PluginData.pluginData[i].setPluginNewVersion(LanguageController.getString("no_version_found"))
                }
            }

            Platform.runLater { setLoadingBar("", false) }

        }.start()
    }

    private fun create() {
        val pluginStage = Stage()
        pluginStage.title = LanguageController.getString("pluginmanager")
        pluginStage.icons.add(ImageLoader.getAppIconSmall())
        pluginStage.isResizable = true
        //infoStage.setMinWidth(500); infoStage.setMinHeight(320);
        pluginStage.width = 700.0
        pluginStage.height = 500.0

        val contentVbox = BorderPane()

        // The UI (Client Area) to display
        contentVbox.center = content

        // The Window as a Scene
        val windowStage = WindowStage("plugin", pluginStage)
        val aboutWindow = WindowScene(windowStage, contentVbox, 1)

        pluginStage.scene = aboutWindow
        pluginStage.show()

        MainScene.stageStack.push(windowStage)

        LogFileHandler.logger.info("open.pluginmanager")
    }

    private fun setLoadingBar(text: String, isProgressIndicatorVisible: Boolean) {
        status!!.text = text
        progressIndicator!!.isVisible = isProgressIndicatorVisible
    }

    private fun setupTable() {
        val pluginName = TableColumn<PluginData, String>(LanguageController.getString("name"))
        val type = TableColumn<PluginData, String>(LanguageController.getString("type"))
        val pluginVersion = TableColumn<PluginData, String>(LanguageController.getString("oldVersion"))
        val newVersion = TableColumn<PluginData, String>(LanguageController.getString("newVersion"))
        val download = TableColumn<PluginData, Any>(LanguageController.getString("download"))

        pluginName.cellValueFactory = PropertyValueFactory("pluginName")
        type.cellValueFactory = PropertyValueFactory("pluginType")
        pluginVersion.cellValueFactory = PropertyValueFactory("pluginOldVersion")
        newVersion.cellValueFactory = PropertyValueFactory("pluginNewVersion")
        //        download.setCellValueFactory(new PropertyValueFactory<>("editButton"));

        // create a cell value factory with an add button for each row in the table.
        download.setCellFactory { personBooleanTableColumn -> AddPluginCell() }

        pluginName.style = "-fx-alignment: CENTER-LEFT;"
        pluginVersion.style = "-fx-alignment: CENTER-LEFT;"
        newVersion.style = "-fx-alignment: CENTER-LEFT;"
        download.style = "-fx-alignment: CENTER-LEFT;"

        pluginTable!!.columns.addAll(pluginName, type, pluginVersion, newVersion, download)
    }

    /** A table cell containing a button for adding a new plugin.  */
    private inner class AddPluginCell internal constructor() : TableCell<PluginData, Any>() {
        private val downloadButton = Button()
        private val progressBar = ProgressBar()

        init {
            downloadButton.isVisible = false
            downloadButton.setOnAction { actionEvent -> startDownload() }
        }

        private fun startDownload() {
            graphic = progressBar

            val fileDownloader: FileDownloader
            try {
                fileDownloader = FileDownloader(URL(Configuration.host + "files/apps/" + AppContext.context.contextIdentifier + "/download/plugins/" + PluginData.pluginData[tableRow.index].pluginName + ".jar"))
            } catch (e: MalformedURLException) {
                if (DebugHelper.DEBUGVERSION) {
                    e.printStackTrace()
                } else {
                    LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e))
                }
                return
            }

            // separate non-FX thread
            // runnable for that thread
            Thread {
                while (fileDownloader.status == FileDownloader.DOWNLOADING) {
                    val progress = fileDownloader.progress.toDouble()
                    Platform.runLater { progressBar.progress = progress }

                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    }

                }

                Platform.runLater {
                    downloadButton.text = LanguageController.getString("install")
                    downloadButton.setOnAction { actionEvent -> installPlugin() }
                    graphic = downloadButton
                }
            }.start()
        }

        private fun installPlugin() {
            graphic = progressBar
            progressBar.progress = ProgressIndicator.INDETERMINATE_PROGRESS

            // separate non-FX thread
            // runnable for that thread
            Thread {
                val pluginFile = File(Configuration.config.jarFile.parentFile.toString() + "/data/update/" + PluginData.pluginData[tableRow.index].pluginName + ".jar")

                //move plugin to plugin directory
                if (pluginFile.exists()) {
                    FileUtil.moveFile(pluginFile.toPath(), Configuration.config.pluginDirFile.toPath())
                } else {
                    println("Plugin does not exist in " + pluginFile.path)
                }

                updateLocalPluginData(Configuration.config.pluginDirFile.toString() + File.separator + PluginData.pluginData[tableRow.index].pluginName + ".jar")

                Platform.runLater { graphic = Label(LanguageController.getString("ready")) }
            }.start()
        }

        private fun updateLocalPluginData(pluginJarPath: String) {
            val updatedData = PluginData.pluginData[tableRow.index]
            val manifestValues: List<String>
            try {
                manifestValues = FileUtil.readManifestPropertiesFromJar(pluginJarPath, "Main-Class", "Version")
                updatedData.setPluginClazz(manifestValues[0])
                updatedData.setPluginOldVersion(manifestValues[1])
            } catch (e: IOException) {
                if (DebugHelper.DEBUGVERSION) {
                    e.printStackTrace()
                } else {
                    LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e))
                }
            }

            PluginData.pluginData[tableRow.index] = updatedData
        }

        /** places an add button in the row only if the row is not empty.  */
        override fun updateItem(item: Any, empty: Boolean) {
            super.updateItem(item, empty)
            if (!empty) {
                if (PluginData.pluginData[tableRow.index].setNewEditButtonName() != null && !downloadButton.isVisible) {
                    downloadButton.text = PluginData.pluginData[tableRow.index].setNewEditButtonName()
                    downloadButton.isDisable = !PluginData.pluginData[tableRow.index].updateEnable
                    downloadButton.isVisible = true
                    graphic = downloadButton
                }
            }
        }
    }
}