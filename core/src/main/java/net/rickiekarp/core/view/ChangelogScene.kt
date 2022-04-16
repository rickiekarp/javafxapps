package net.rickiekarp.core.view

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.AppDatabase
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.model.ChangelogEntry
import net.rickiekarp.core.net.NetResponse
import net.rickiekarp.core.net.NetworkApi
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.util.parser.XmlParser
import net.rickiekarp.core.ui.windowmanager.ThemeSelector
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException
import java.io.IOException
import java.util.ArrayList

/**
 * This class is used for creating different message dialogs.
 * Example: Error Message
 */
class ChangelogScene {

    /**
     * Compares local and remote program versions
     * @return Returns update status ID as an integer
     */
    private val changelogList: ArrayList<ChangelogEntry>
        get() {
            val changelogList = ArrayList<ChangelogEntry>()
            val remoteVersionsXml = NetResponse.getResponseString(AppContext.context.networkApi.runNetworkAction(NetworkApi.requestChangelog()))

            try {
                val doc = XmlParser.stringToDom(remoteVersionsXml!!)
                doc.documentElement.normalize()

                val moduleList = doc.getElementsByTagName("change")

                if (moduleList.length > 0) {
                    var labTest: Element
                    for (i in 0 until moduleList.length) {
                        labTest = moduleList.item(i) as Element
                        changelogList.add(ChangelogEntry(labTest.getAttribute("version"), "1", labTest.textContent))
                        LogFileHandler.logger.info("Changelog: " + labTest.getAttribute("version") + " - " + labTest.textContent)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: SAXException) {
                e.printStackTrace()
            } catch (e: ParserConfigurationException) {
                e.printStackTrace()
            }

            return changelogList
        }

    init {
        create(500, 400)
    }

    fun create(width: Int, height: Int) {
        val stage = Stage()
        stage.icons.add(ImageLoader.getAppIconSmall())
        stage.width = (width + 50).toDouble()
        stage.height = (height + 50).toDouble()
        stage.minWidth = width.toDouble()
        stage.minHeight = height.toDouble()
        stage.title = LanguageController.getString("changelog")

        //Layout
        val contentPane = BorderPane()

        val vbox = VBox()
        vbox.padding = Insets(0.0, 0.0, 0.0, 20.0)

        val controls = HBox()
        controls.padding = Insets(10.0, 0.0, 10.0, 0.0)  //padding top, left, bottom, right
        controls.alignment = Pos.CENTER

        val progress = ProgressIndicator()
        progress.progress = ProgressIndicator.INDETERMINATE_PROGRESS
        vbox.children.add(progress)

        val listview = ListView<String>()
        listview.padding = Insets(10.0, 0.0, 0.0, 0.0)
        listview.style = "-fx-font-size: 11pt;"

        val okButton = Button("OK")
        okButton.setOnAction { arg0 -> stage.close() }
        controls.children.add(okButton)

        // The UI (Client Area) to display
        contentPane.center = vbox
        contentPane.bottom = controls

        val modalDialogScene = WindowScene(WindowStage("changelog", stage), contentPane, 1)
        ThemeSelector.setTheme(modalDialogScene, this.javaClass.classLoader)

        stage.scene = modalDialogScene
        stage.show()

        // load changelog data
        Thread {

            if (AppDatabase.changelogTreeMap == null) {
                AppDatabase.changelogTreeMap = changelogList
            }

            Platform.runLater {
                if (AppDatabase.changelogTreeMap!!.size > 0) {
                    vbox.children.remove(progress)
                    vbox.children.add(listview)
                    for (i in AppDatabase.changelogTreeMap!!.indices) {
                        listview.items.addAll(
                                "${AppDatabase.changelogTreeMap!![i].getVersion()} - ${AppDatabase.changelogTreeMap!![i].getDesc()}"
                        )
                    }
                } else {
                    val message = Label(LanguageController.getString("changelog_not_found"))
                    vbox.alignment = Pos.CENTER
                    vbox.children.remove(progress)
                    vbox.children.add(message)
                }
            }
        }.start()

        LogFileHandler.logger.info("open.ChangelogScene")
    }
}
