package net.rickiekarp.qaacc.view

import net.rickiekarp.core.components.textfield.CustomTextField
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.qaacc.factory.AccountXmlFactory
import net.rickiekarp.qaacc.model.Account
import net.rickiekarp.qaacc.settings.AppConfiguration
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.stage.Stage
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import java.io.IOException
import java.net.MalformedURLException
import java.util.logging.Level

class AccountEditDialog internal constructor(GAME_ID: Int, sceneType: String, selectedIdx: Int, selectedItem: Account?) {
    private var accEditScene: WindowScene? = null


    init {
        val accEdit = editDialog
        if (accEdit == null) {
            editDialog = this
            create(GAME_ID, sceneType, selectedIdx, selectedItem!!)
        } else {
            if (accEdit.accEditScene!!.win.windowStage.stage.isShowing) {
                accEdit.accEditScene!!.win.windowStage.stage.requestFocus()
            } else {
                editDialog = this
                create(GAME_ID, sceneType, selectedIdx, selectedItem!!)
            }
        }
    }

    private fun create(GAME_ID: Int, SceneType: String, selectedIdx: Int, selectedItem: Account) {
        val editStage = Stage()
        editStage.width = 360.0
        editStage.height = 380.0
        when (SceneType) {
            "new" -> {
                editStage.title = LanguageController.getString("addAcc")
                editStage.icons.add(ImageLoader.getAppIconSmall())
            }

            "edit" -> {
                editStage.title = LanguageController.getString("editAcc")
                editStage.icons.add(ImageLoader.getAppIconSmall())
            }
        }
        editStage.isResizable = false

        val borderpane = BorderPane()
        val contentNode = getLayout(GAME_ID, SceneType, selectedIdx, selectedItem)

        // The UI (Client Area) to display
        borderpane.center = contentNode

        accEditScene = WindowScene(WindowStage("edit", editStage), borderpane, 1)

        editStage.scene = accEditScene
        editStage.show()

        //if (DebugHelper.isDebugVersion()) { DebugHelper.debugAccEdit(); }
        LogFileHandler.logger.log(Level.INFO, "open.AccountEditDialog{$SceneType,$selectedIdx}")
    }


    private fun getLayout(GAME_ID: Int, SceneType: String, selectedIdx: Int, selectedItem: Account): Node {

        val mainContent = BorderPane()
        mainContent.styleClass.add("background")

        val anchor = AnchorPane()
        val maingrid = GridPane()

        val column1 = ColumnConstraints()
        column1.percentWidth = 40.0
        val column2 = ColumnConstraints()
        column2.percentWidth = 60.0
        maingrid.columnConstraints.addAll(column1, column2)
        maingrid.vgap = 15.0

        AnchorPane.setTopAnchor(maingrid, 15.0)
        AnchorPane.setRightAnchor(maingrid, 15.0)
        AnchorPane.setLeftAnchor(maingrid, 15.0)
        AnchorPane.setBottomAnchor(maingrid, 15.0)

        val controls = HBox()

        //add components
        val accName = Label(LanguageController.getString("name"))
        GridPane.setConstraints(accName, 0, 0)
        maingrid.children.add(accName)

        accNameTF = TextField()
        GridPane.setConstraints(accNameTF, 1, 0)
        maingrid.children.add(accNameTF)

        val accMail = Label(LanguageController.getString("mail"))
        GridPane.setConstraints(accMail, 0, 1)
        maingrid.children.add(accMail)

        accMailTF = TextField()
        GridPane.setConstraints(accMailTF, 1, 1)
        maingrid.children.add(accMailTF)

        val accLevel = Label(LanguageController.getString("level"))
        GridPane.setConstraints(accLevel, 0, 2)
        maingrid.children.add(accLevel)

        accLevelTF = CustomTextField()
        accLevelTF.setRestrict("[0-9]")
        GridPane.setConstraints(accLevelTF, 1, 2)
        maingrid.children.add(accLevelTF)

        val accAlliance = Label()
        GridPane.setConstraints(accAlliance, 0, 3)
        maingrid.children.add(accAlliance)

        accAllianceTF = TextField()
        GridPane.setConstraints(accAllianceTF, 1, 3)
        maingrid.children.add(accAllianceTF)


        //controls
        val saveCfg = Button()
        controls.children.add(saveCfg)

        controls.padding = Insets(15.0, 12.0, 15.0, 12.0)  //padding top, left, bottom, right
        controls.spacing = 10.0
        controls.alignment = Pos.CENTER_RIGHT

        when (SceneType) {
            "new" -> {
                saveCfg.text = LanguageController.getString("addAcc")
                saveCfg.setOnAction { event ->

                    if (accNameTF.text.isEmpty()) {
                        MessageDialog(0, LanguageController.getString("enterName"), 350, 220)
                    } else {
                        try {
                            AccountXmlFactory.addAccount(GAME_ID, accNameTF.text, accMailTF.text, accLevelTF.text, accAllianceTF.text)
                        } catch (e1: TransformerException) {
                            if (DebugHelper.DEBUGVERSION) {
                                e1.printStackTrace()
                            } else {
                                ExceptionHandler(Thread.currentThread(), e1)
                            }
                        } catch (e1: ParserConfigurationException) {
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
                        } catch (e1: SAXException) {
                            if (DebugHelper.DEBUGVERSION) {
                                e1.printStackTrace()
                            } else {
                                ExceptionHandler(Thread.currentThread(), e1)
                            }
                        }

                        AppConfiguration.accountData.add(Account(
                                accNameTF.text,
                                accMailTF.text,
                                accLevelTF.text,
                                accAllianceTF.text))
                        accEditScene!!.win.windowStage.stage.close()
                        AccountOverview.status.style = "-fx-text-fill: #55c4fe;"
                        AccountOverview.status.text = LanguageController.getString("accAdded")
                        LogFileHandler.logger.log(Level.INFO, "new account added: " + accNameTF.text)

                        if (AppConfiguration.accountData.size == 1) {
                            AccountOverview.accCount.text = "1 " + LanguageController.getString("acc_loaded")
                            AccountOverview.editAcc.isDisable = false
                            AccountOverview.delAcc.isDisable = false
                            AccountOverview.tableview.selectionModel.select(0)
                        } else {
                            AccountOverview.accCount.text = AppConfiguration.accountData.size.toString() + " " + LanguageController.getString("accs_loaded")
                        }
                    }


                }
            }

            "edit" -> {
                saveCfg.text = LanguageController.getString("saveAcc")
                saveCfg.setOnAction { event ->
                    try {
                        AccountXmlFactory.saveAccXml(GAME_ID, selectedIdx)
                        accEditScene!!.win.windowStage.stage.close()
                    } catch (e1: MalformedURLException) {
                        if (DebugHelper.DEBUGVERSION) {
                            e1.printStackTrace()
                        } else {
                            ExceptionHandler(Thread.currentThread(), e1)
                        }
                    }

                    Account.setAccount(selectedItem)
                    AccountOverview.refreshPersonTable(selectedIdx)
                }
            }
        }

        //set project specific names
        when (GAME_ID) {
            2 -> accAlliance.text = LanguageController.getString("cooperative")
            else -> accAlliance.text = LanguageController.getString("alliance")
        }

        anchor.children.add(maingrid)

        //set borderpane layout
        mainContent.center = anchor
        mainContent.bottom = controls


        accNameTF.textProperty().addListener { observable, oldValue, newValue ->
            try {
                if (newValue.length > 16)
                //maxLength of text field
                    accNameTF.text = oldValue
            } catch (e: Exception) {
                accNameTF.text = oldValue
            }
        }

        return mainContent
    }

    companion object {
        private var editDialog: AccountEditDialog? = null
        lateinit var accNameTF: TextField
        lateinit var accMailTF: TextField
        lateinit var accLevelTF: CustomTextField
        lateinit var accAllianceTF: TextField
    }
}
