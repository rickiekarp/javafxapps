package net.rickiekarp.appupdater.ui

import net.rickiekarp.appupdater.UpdateMain
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import net.rickiekarp.core.AppContext

import java.io.IOException
import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.jar.Manifest

class UpdateCheckerGUI : Stage() {
    private var textArea: TextArea? = null
    private var btnInstall: Button? = null

    var message: String
        get() = textArea!!.text
        set(msg) {
            textArea!!.text = msg
        }

    init {

        initComponents()

        //readManifestVersion();

        //checkForUpdate();
    }

    private fun initComponents() {

        val modalDialog = Stage()
        modalDialog.width = 400.0
        modalDialog.height = 300.0
        modalDialog.title = "Updater"

        val borderpane = BorderPane()
        borderpane.padding = Insets(10.0, 10.0, 10.0, 10.0)

        val options = AnchorPane()
        options.minHeight = 50.0

        val optionHBox = HBox()

        //components
        textArea = TextArea()
        textArea!!.isEditable = false

        btnInstall = Button("Install!")
        btnInstall!!.isDisable = true
        btnInstall!!.setMinSize(100.0, 30.0)

        optionHBox.children.addAll(btnInstall)

        //ActionListener
        btnInstall!!.setOnAction { textArea!!.appendText("Installing update! Please wait...\n") }

        // The UI (Client Area) to display
        options.children.addAll(optionHBox)

        AnchorPane.setRightAnchor(optionHBox, 5.0)
        AnchorPane.setBottomAnchor(optionHBox, 5.0)

        borderpane.center = textArea
        borderpane.bottom = options

        val modalDialogScene = Scene(borderpane)

        modalDialog.scene = modalDialogScene
        modalDialog.show()
    }

    fun appendMessage(msg: String) {
        textArea!!.appendText("\n" + msg)
    }

    private fun readManifestVersion() {

        var version: String

        //read version from main jar manifest
        try {
            val manifest = JarFile(UpdateMain.getArgs()[1]).manifest
            val attributes = manifest.mainAttributes
            version = attributes.getValue("Version")
        } catch (e: IOException) {
            println("Error while reading version: " + e.message)
            version = "DEV"
        }

        textArea!!.appendText("Current program version: $version\n")

    }

    private fun checkForUpdate() {


        textArea!!.appendText("Contacting download server...\n")







        textArea!!.appendText("Success\n")


        btnInstall!!.isDisable = false

    }

    companion object {
        var updateChecker: UpdateCheckerGUI? = null
    }
}