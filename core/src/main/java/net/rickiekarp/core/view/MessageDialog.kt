package net.rickiekarp.core.view

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.net.update.UpdateChecker
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.stage.Modality
import javafx.stage.Stage

import java.io.IOException
import java.net.URISyntaxException

/**
 * This class is used for creating different message dialogs.
 * Example: Error Message
 */
class MessageDialog(type: Int, msg: String, width: Int, height: Int) {

    init {
        when (type) {
            0 -> createDialog("error", msg, width, height)
            1 -> createDialog("info", msg, width, height)
        }
    }

    companion object {

        private fun createDialog(title: String, msg: String, width: Int, height: Int) {
            val modalDialog = Stage()
            modalDialog.icons.add(ImageLoader.getAppIconSmall())
            modalDialog.initModality(Modality.APPLICATION_MODAL)
            modalDialog.isResizable = false
            modalDialog.width = width.toDouble()
            modalDialog.height = height.toDouble()
            modalDialog.title = LanguageController.getString(title)

            //Layout
            val contentPane = BorderPane()

            val vbox = VBox()
            vbox.padding = Insets(20.0, 0.0, 0.0, 20.0)

            val controls = HBox()
            controls.padding = Insets(10.0, 0.0, 10.0, 0.0)  //padding top, left, bottom, right
            controls.alignment = Pos.CENTER

            //Components
            val label = Label(msg)
            vbox.children.addAll(label)
            label.isWrapText = true

            val okButton = Button("OK")
            okButton.setOnAction { arg0 -> modalDialog.close() }
            controls.children.add(okButton)

            // The UI (Client Area) to display
            contentPane.center = vbox
            contentPane.bottom = controls

            val modalDialogScene = WindowScene(WindowStage("message", modalDialog), contentPane, 1)

            modalDialog.scene = modalDialogScene
            modalDialog.show()

            LogFileHandler.logger.info("open.MessageDialog($title)")
        }

        fun confirmDialog(msg: String, width: Int, height: Int): Boolean {
            val modalDialog = Stage()
            modalDialog.icons.add(ImageLoader.getAppIconSmall())
            modalDialog.initModality(Modality.APPLICATION_MODAL)
            modalDialog.isResizable = false
            modalDialog.width = width.toDouble()
            modalDialog.height = height.toDouble()
            modalDialog.title = LanguageController.getString("confirm")

            val bool = BooleanArray(1)

            val borderpane = BorderPane()

            val contentVbox = VBox()
            contentVbox.spacing = 20.0

            val optionHBox = HBox()
            optionHBox.spacing = 20.0
            optionHBox.alignment = Pos.CENTER
            optionHBox.padding = Insets(5.0, 0.0, 15.0, 0.0)

            //components
            val label = Label(LanguageController.getString(msg))
            label.isWrapText = true
            label.padding = Insets(20.0, 10.0, 10.0, 20.0)

            val yesButton = Button(LanguageController.getString("yes"))
            yesButton.setOnAction { event ->
                bool[0] = true
                modalDialog.close()
            }

            val noButton = Button(LanguageController.getString("no"))
            noButton.setOnAction { event ->
                bool[0] = false
                modalDialog.close()
            }

            if (DebugHelper.isDebugVersion) {
                contentVbox.style = "-fx-background-color: gray"
                optionHBox.style = "-fx-background-color: #444444;"
            }

            optionHBox.children.addAll(yesButton, noButton)

            // The UI (Client Area) to display
            contentVbox.children.addAll(label, optionHBox)
            VBox.setVgrow(contentVbox, Priority.ALWAYS)

            borderpane.center = contentVbox
            borderpane.bottom = optionHBox

            // The Window as a Scene
            val modalDialogScene = WindowScene(WindowStage("confirm", modalDialog), borderpane, 1)

            modalDialog.scene = modalDialogScene

            LogFileHandler.logger.info("open.confirmDialog")

            modalDialog.showAndWait()

            return bool[0]
        }

        fun restartDialog(msg: String, width: Int, height: Int): Boolean {
            val modalDialog = Stage()
            modalDialog.icons.add(ImageLoader.getAppIconSmall())
            modalDialog.initModality(Modality.APPLICATION_MODAL)
            modalDialog.isResizable = false
            modalDialog.width = width.toDouble()
            modalDialog.height = height.toDouble()
            modalDialog.title = LanguageController.getString("restartApp")

            val bool = BooleanArray(1)

            val borderpane = BorderPane()

            val contentVbox = VBox()
            contentVbox.spacing = 20.0

            val optionHBox = HBox()
            optionHBox.spacing = 20.0
            optionHBox.alignment = Pos.CENTER
            optionHBox.padding = Insets(5.0, 0.0, 15.0, 0.0)

            //components
            val label = Label(LanguageController.getString(msg))
            label.isWrapText = true
            label.padding = Insets(20.0, 10.0, 10.0, 20.0)

            val yesButton = Button(LanguageController.getString("yes"))
            yesButton.setOnAction { event ->
                try {
                    //save settings
                    try {
                        Configuration.config.save()
                    } catch (e1: Exception) {
                        if (DebugHelper.DEBUGVERSION) {
                            e1.printStackTrace()
                        } else {
                            ExceptionHandler(Thread.currentThread(), e1)
                        }
                    }

                    //restart
                    DebugHelper.restartApplication()
                } catch (e1: URISyntaxException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                } catch (e1: IOException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                }
            }

            val noButton = Button(LanguageController.getString("restartLater"))
            noButton.setOnAction { event ->
                bool[0] = false
                modalDialog.close()
            }

            if (DebugHelper.isDebugVersion) {
                contentVbox.style = "-fx-background-color: gray"
                optionHBox.style = "-fx-background-color: #444444;"
            }

            optionHBox.children.addAll(yesButton, noButton)

            // The UI (Client Area) to display
            contentVbox.children.addAll(label, optionHBox)
            VBox.setVgrow(contentVbox, Priority.ALWAYS)

            borderpane.center = contentVbox
            borderpane.bottom = optionHBox

            // The Window as a Scene
            val modalDialogScene = WindowScene(WindowStage("restart", modalDialog), borderpane, 1)

            modalDialog.scene = modalDialogScene

            LogFileHandler.logger.info("open.errorMessageDialog")

            modalDialog.showAndWait()

            return bool[0]
        }

        fun installUpdateDialog(msg: String, width: Int, height: Int): Stage {
            val modalDialog = Stage()
            modalDialog.icons.add(ImageLoader.getAppIconSmall())
            modalDialog.initModality(Modality.APPLICATION_MODAL)
            modalDialog.isResizable = false
            modalDialog.width = width.toDouble()
            modalDialog.height = height.toDouble()
            modalDialog.title = LanguageController.getString("installUpdate")

            val borderpane = BorderPane()

            val contentVbox = VBox()
            contentVbox.spacing = 20.0

            val options = AnchorPane()
            options.minHeight = 50.0

            val optionHBox = HBox()
            optionHBox.spacing = 10.0

            //components
            val label = Label(LanguageController.getString("update_desc"))
            label.isWrapText = true
            label.padding = Insets(20.0, 10.0, 10.0, 20.0)

            val remember = CheckBox(LanguageController.getString("hideThis"))
            remember.isDisable = true
            remember.setOnAction { event1 -> println(remember.isSelected) }

            val yesButton = Button(LanguageController.getString("yes"))
            yesButton.setOnAction { event ->
                try {
                    //remove tray icon before installing update
                    //                if (ToolTrayIcon.icon != null ) {
                    //                    if (ToolTrayIcon.icon.getSystemTray().getTrayIcons().length > 0) {
                    //                        ToolTrayIcon.icon.removeTrayIcon();
                    //                    }
                    //                }
                    UpdateChecker.installUpdate()
                } catch (e1: URISyntaxException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                } catch (e1: IOException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                }
            }

            val noButton = Button(LanguageController.getString("no"))
            noButton.setOnAction { event -> modalDialog.close() }

            //        if (DebugHelper.isDebugVersion()) {
            //            contentVbox.setStyle("-fx-background-color: gray");
            //            optionHBox.setStyle("-fx-background-color: #444444;");
            //        }

            optionHBox.children.addAll(yesButton, noButton)

            // The UI (Client Area) to display
            contentVbox.children.addAll(label)
            options.children.addAll(remember, optionHBox)

            AnchorPane.setRightAnchor(optionHBox, 5.0)
            AnchorPane.setBottomAnchor(optionHBox, 5.0)
            AnchorPane.setLeftAnchor(remember, 10.0)
            AnchorPane.setBottomAnchor(remember, 10.0)



            borderpane.center = contentVbox
            borderpane.bottom = options

            // The Window as a Scene
            val modalDialogScene = WindowScene(WindowStage("installUpdate", modalDialog), borderpane, 1)


            modalDialog.scene = modalDialogScene

            LogFileHandler.logger.info("open.installUpdateDialog")

            return modalDialog
        }
    }
}
