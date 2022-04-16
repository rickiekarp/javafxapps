package net.rickiekarp.flc.view.dialogs

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.flc.tasks.ListTask
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * This class is used for creating the 'In progress' loading dialog.
 */
class ProgressDialog : Stage() {
    init {
        createProgressDialog(400, 250)
    }

    private fun createProgressDialog(width: Int, height: Int) {
        this.icons.add(ImageLoader.getAppIconSmall())
        this.initModality(Modality.APPLICATION_MODAL)
        this.isResizable = false
        this.width = width.toDouble()
        this.height = height.toDouble()
        this.title = LanguageController.getString("inprogress")

        // The UI (Client Area) to display
        val borderpane = BorderPane()

        val vbox = VBox()
        vbox.spacing = 15.0
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(30.0)

        val controls = AnchorPane()
        controls.padding = Insets(10.0, 7.0, 10.0, 7.0)  //padding top, left, bottom, right

        // Components
        val label = Label(LanguageController.getString("scanning"))

        val loadBar = ProgressBar(0.0)
        loadBar.progress = ProgressIndicator.INDETERMINATE_PROGRESS

        val abort = Button(LanguageController.getString("abort"))

        // Add components to layout
        vbox.children.addAll(label, loadBar)
        controls.children.add(abort)
        AnchorPane.setRightAnchor(abort, 7.0)
        AnchorPane.setBottomAnchor(abort, 0.0)

        borderpane.center = vbox
        borderpane.bottom = controls

        // The Window as a Scene
        val modalDialogScene = WindowScene(WindowStage("progress", this), borderpane, 1)

        this.scene = modalDialogScene
        this.show()

        LogFileHandler.logger.info("open.progressDialog")

        if (DebugHelper.isDebugVersion) {
            vbox.style = "-fx-background-color: gray"
            controls.style = "-fx-background-color: #444444;"
        }

        abort.setOnAction { ListTask.listTask.cancel() }
    }

    override fun close() {
        Platform.runLater { this.fireEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST)) }
    }
}
