package net.rickiekarp.qaacc.view

import net.rickiekarp.core.components.textfield.CustomTextField
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.util.CommonUtil
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.core.view.layout.AppLayout
import net.rickiekarp.qaacc.factory.AccountXmlFactory
import net.rickiekarp.qaacc.model.Account
import net.rickiekarp.qaacc.settings.AppConfiguration
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import java.io.IOException
import java.util.logging.Level

class MainLayout : AppLayout {
    private var findAccount: Button? = null
    private var saveAccount: Button? = null
    private var nameTextField: TextField? = null
    private var mailTextField: TextField? = null
    private var passTextField: TextField? = null
    private var acronymTextField: CustomTextField? = null
    private var gameComboBox: ComboBox<String>? = null
    private var logo: ImageView? = null
    private var inputGrid: GridPane? = null
    private var outputGrid: GridPane? = null
    private var controls: HBox? = null

    override val layout: Node
        get() = getMainLayout()

    fun getMainLayout(): Node {
        val mainContent = BorderPane()

        inputGrid = GridPane()
        outputGrid = GridPane()
        val vbox = VBox()
        controls = HBox()

        //set Layout
        val column1 = ColumnConstraints()
        column1.percentWidth = 40.0
        val column2 = ColumnConstraints()
        column2.percentWidth = 20.0
        val column3 = ColumnConstraints()
        column3.percentWidth = 40.0
        inputGrid!!.columnConstraints.addAll(column1, column2, column3) // each get 33% of width

        val column1a = ColumnConstraints()
        column1a.percentWidth = 20.0
        val column2a = ColumnConstraints()
        column2a.percentWidth = 60.0
        val column3a = ColumnConstraints()
        column3a.percentWidth = 20.0
        outputGrid!!.columnConstraints.addAll(column1a, column2a, column3a) // each get 33% of width


        inputGrid!!.hgap = 10.0
        inputGrid!!.vgap = 10.0
        inputGrid!!.padding = Insets(20.0, 5.0, 0.0, 5.0)  //padding top, left, bottom, right
        outputGrid!!.hgap = 5.0
        outputGrid!!.vgap = 10.0
        outputGrid!!.padding = Insets(20.0, 5.0, 0.0, 5.0)  //padding top, left, bottom, right

        vbox.padding = Insets(5.0)

        controls!!.padding = Insets(5.0, 5.0, 5.0, 5.0)

        //add Grids to VBox Layout
        vbox.children.add(0, inputGrid)
        vbox.children.add(1, outputGrid)

        //add components
        val acronymLabel = Label(LanguageController.getString("acronym_input"))
        GridPane.setConstraints(acronymLabel, 0, 0)
        inputGrid!!.children.add(acronymLabel)

        acronymTextField = CustomTextField()
        acronymTextField!!.setMaxLength(4)
        GridPane.setConstraints(acronymTextField, 1, 0)
        inputGrid!!.children.add(acronymTextField)

        logo = ImageView(ImageLoader.getAppIconSmall())
        logo!!.fitHeightProperty().setValue(50)
        logo!!.fitWidthProperty().setValue(50)
        GridPane.setConstraints(logo, 2, 0)
        inputGrid!!.children.add(logo)

        nameTextField = TextField()
        nameTextField!!.isEditable = false
        nameTextField!!.alignment = Pos.CENTER
        nameTextField!!.tooltip = Tooltip(LanguageController.getString("name"))
        GridPane.setConstraints(nameTextField, 0, 0)
        outputGrid!!.children.add(nameTextField)

        mailTextField = TextField()
        mailTextField!!.isEditable = false
        mailTextField!!.alignment = Pos.CENTER
        mailTextField!!.tooltip = Tooltip(LanguageController.getString("mail"))
        GridPane.setConstraints(mailTextField, 1, 0)
        outputGrid!!.children.add(mailTextField)

        passTextField = TextField()
        passTextField!!.isEditable = false
        passTextField!!.alignment = Pos.CENTER
        passTextField!!.tooltip = Tooltip(LanguageController.getString("password"))
        GridPane.setConstraints(passTextField, 2, 0)
        outputGrid!!.children.add(passTextField)

        val copyNameBtn = Button(LanguageController.getString("copy"))
        copyNameBtn.tooltip = Tooltip(LanguageController.getString("copy_name_desc"))
        GridPane.setConstraints(copyNameBtn, 0, 1)
        outputGrid!!.children.add(copyNameBtn)

        val copyMailBtn = Button(LanguageController.getString("copy"))
        copyMailBtn.tooltip = Tooltip(LanguageController.getString("copy_mail_desc"))
        GridPane.setConstraints(copyMailBtn, 1, 1)
        outputGrid!!.children.add(copyMailBtn)

        val copyPassBtn = Button(LanguageController.getString("copy"))
        copyPassBtn.tooltip = Tooltip(LanguageController.getString("copy_pass_desc"))
        GridPane.setConstraints(copyPassBtn, 2, 1)
        outputGrid!!.children.add(copyPassBtn)

        gameComboBox = ComboBox()
        val mainAppWidth = 750
        gameComboBox!!.setPrefSize(mainAppWidth / 3.8, 40.0)
        GridPane.setConstraints(gameComboBox, 0, 0)

        status = Label()
        status.setPrefSize(mainAppWidth / 4.5, 40.0)
        GridPane.setConstraints(status, 1, 0)

        findAccount = Button(LanguageController.getString("account_manager"))
        findAccount!!.tooltip = Tooltip(LanguageController.getString("open_account_manager"))
        findAccount!!.setPrefSize((mainAppWidth / 4).toDouble(), 40.0)
        GridPane.setConstraints(findAccount, 2, 0)

        saveAccount = Button(LanguageController.getString("saveAcc"))
        saveAccount!!.tooltip = Tooltip(LanguageController.getString("saveAcc"))
        saveAccount!!.setPrefSize((mainAppWidth / 4).toDouble(), 40.0)
        GridPane.setConstraints(saveAccount, 3, 0)

        controls!!.children.addAll(gameComboBox, status, findAccount, saveAccount)
        controls!!.spacing = 5.0

        //Center components in GridPane
        GridPane.setHalignment(acronymLabel, HPos.CENTER)
        GridPane.setHalignment(logo, HPos.CENTER)
        GridPane.setHalignment(copyNameBtn, HPos.CENTER)
        GridPane.setHalignment(copyMailBtn, HPos.CENTER)
        GridPane.setHalignment(copyPassBtn, HPos.CENTER)

        //add vbox & controls pane to borderpane layout
        mainContent.center = vbox
        mainContent.bottom = controls

        copyNameBtn.setOnAction { event ->
            AppConfiguration.setStringToClipboard(nameTextField!!.text)
            status.text = LanguageController.getString("name_copied")
            LogFileHandler.logger.log(Level.INFO, "copy2clipboard.name")
        }

        copyMailBtn.setOnAction { event ->
            AppConfiguration.setStringToClipboard(mailTextField!!.text)
            status.text = LanguageController.getString("mail_copied")
            LogFileHandler.logger.log(Level.INFO, "copy2clipboard.mail")
        }

        copyPassBtn.setOnAction { event ->
            AppConfiguration.setStringToClipboard(passTextField!!.text)
            status.text = LanguageController.getString("password_copied")
            LogFileHandler.logger.log(Level.INFO, "copy2clipboard.pass")
        }


        findAccount!!.setOnAction { event ->
            when (AppConfiguration.pjState) {
                -1 -> MessageDialog(0, LanguageController.getString("project_not_selected"), 350, 200)
                else -> AccountOverview(gameComboBox!!.selectionModel.selectedIndex)
            }
        }

        saveAccount!!.setOnAction { event ->
            when (AppConfiguration.pjState) {
                -1 -> MessageDialog(0, LanguageController.getString("project_not_selected"), 350, 200)
                else -> {
                    if (acronymTextField!!.text.isEmpty()) {
                        MessageDialog(0, LanguageController.getString("acronymTextField_empty"), 400, 200)
                    } else {
                        try {
                            AccountXmlFactory.addAccount(AppConfiguration.pjState, nameTextField!!.text, mailTextField!!.text, "1", "")
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
                        } catch (e1: TransformerException) {
                            if (DebugHelper.DEBUGVERSION) {
                                e1.printStackTrace()
                            } else {
                                ExceptionHandler(Thread.currentThread(), e1)
                            }
                        }

                    }

                    AppConfiguration.accountData.add(Account(nameTextField!!.text, mailTextField!!.text, "1", ""))

                    if (AccountOverview.overview != null && AccountOverview.overview!!.accOverviewScene!!.win.windowStage.stage.isShowing) {
                        AccountOverview.refreshPersonTable(-1)
                    }
                }
            }
        }


        acronymTextField!!.setOnKeyReleased { ke ->
            if (acronymTextField!!.text == "") {
                nameTextField!!.text = ""
                mailTextField!!.text = ""
                passTextField!!.text = ""
            } else {
                nameTextField!!.text = acronymTextField!!.text + CommonUtil.getDate("ddMMyy") + AppConfiguration.dept
                mailTextField!!.text = AppConfiguration.mail_pref + acronymTextField!!.text + CommonUtil.getDate("ddMMyy") + AppConfiguration.mail_end
                passTextField!!.text = AppConfiguration.pass
            }
        }

        if (AppConfiguration.projectData.size > 0) {
            gameComboBox!!.valueProperty().addListener { ov, t, t1 ->
                try {
                    if (gameComboBox!!.value == LanguageController.getString("projSel")) {
                        AppConfiguration.pjState = -1
                        gameComboBox!!.tooltip = Tooltip(LanguageController.getString("project_not_selected"))
                        logo!!.image = ImageLoader.getAppIconSmall()
                    } else if (gameComboBox!!.value == AppConfiguration.projectData[0].getProjectName()) {
                        AppConfiguration.pjState = 0
                        gameComboBox!!.tooltip = Tooltip(AppConfiguration.projectData[0].getProjectName())
                        status.text = gameComboBox!!.value + " " + LanguageController.getString("selected")
                        logo!!.image = ImageLoader.getAppIconSmall()
                    } else if (gameComboBox!!.value == AppConfiguration.projectData[1].getProjectName()) {
                        AppConfiguration.pjState = 1
                        gameComboBox!!.tooltip = Tooltip(AppConfiguration.projectData[1].getProjectName())
                        status.text = gameComboBox!!.value + " " + LanguageController.getString("selected")
                        logo!!.image = ImageLoader.getAppIconSmall()
                    } else if (gameComboBox!!.value == AppConfiguration.projectData[2].getProjectName()) {
                        AppConfiguration.pjState = 2
                        gameComboBox!!.tooltip = Tooltip(AppConfiguration.projectData[2].getProjectName())
                        status.text = gameComboBox!!.value + " " + LanguageController.getString("selected")
                        logo!!.image = ImageLoader.getAppIconSmall()
                    } else if (gameComboBox!!.value == AppConfiguration.projectData[3].getProjectName()) {
                        AppConfiguration.pjState = 3
                        gameComboBox!!.tooltip = Tooltip(AppConfiguration.projectData[3].getProjectName())
                        status.text = gameComboBox!!.value + " " + LanguageController.getString("selected")
                        logo!!.image = ImageLoader.getAppIconSmall()
                    } else {
                        AppConfiguration.pjState = gameComboBox!!.selectionModel.selectedIndex
                        gameComboBox!!.tooltip = Tooltip(AppConfiguration.projectData[AppConfiguration.pjState].getProjectName())
                        status.text = gameComboBox!!.value + " " + LanguageController.getString("selected")
                        logo!!.image = ImageLoader.getAppIconSmall()
                    }
                } catch (e1: Exception) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                }
            }
        }

        acronymTextField!!.requestFocus()
        //        if (!AppConfiguration.acronym.isEmpty()) { acronymTextField.setText(AppConfiguration.acronym); createName(); } //set acronym setting

        debugMain()

        return mainContent
    }

    //create name after reading config file
    internal fun createName() {
        if (acronymTextField!!.text == "") {
            nameTextField!!.text = ""
            mailTextField!!.text = ""
            passTextField!!.text = ""
        } else {
            nameTextField!!.text = acronymTextField!!.text + CommonUtil.getDate("ddMMyy") + AppConfiguration.dept
            mailTextField!!.text = AppConfiguration.mail_pref + acronymTextField!!.text + CommonUtil.getDate("ddMMyy") + AppConfiguration.mail_end
            passTextField!!.text = AppConfiguration.pass
        }
        status.text = LanguageController.getString("ready")
    }

    /** sets the items of the project ComboBox  */
    fun fillComboBox() {
        if (AppConfiguration.projectData.size != 0) {
            for (i in AppConfiguration.projectData.indices) {
                gameComboBox!!.items.add(AppConfiguration.projectData[i].getProjectName())
            }

            if (AppConfiguration.pjCfgSelection == -1) {
                gameComboBox!!.setValue(LanguageController.getString("projSel"))
            } else {
                gameComboBox!!.value = AppConfiguration.projectData[AppConfiguration.pjCfgSelection].getProjectName()
                status.text = LanguageController.getString("ready")
            }
        } else {
            gameComboBox!!.value = LanguageController.getString("noProjSel")
            gameComboBox!!.tooltip = Tooltip(LanguageController.getString("project_not_found_desc"))
            findAccount!!.isDisable = true
            saveAccount!!.isDisable = true
        }
    }

    private fun debugMain() {
        if (DebugHelper.isDebugVersion) {
            inputGrid!!.style = "-fx-background-color: #155ff9;"
            outputGrid!!.style = "-fx-background-color: #536699;"
            controls!!.style = "-fx-background-color: #336699;"
        } else {
            inputGrid!!.style = null
            outputGrid!!.style = null
            controls!!.style = null
        }
    }

    override fun postInit() {

    }

    companion object {
        var status: Label = Label()
    }
}
