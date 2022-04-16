package net.rickiekarp.qaacc.view

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.util.CommonUtil
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.qaacc.factory.AccountXmlFactory
import net.rickiekarp.qaacc.settings.AppConfiguration
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage

import java.net.MalformedURLException

class BugReportSettings(projectID: Int) {
    var bugReportSettings: WindowScene? = null
        private set
    private var accountTP: Label? = null
    private var versionTF: TextField? = null
    private var tmplTA: TextArea? = null


    init {
        val bugRep = BugReportSettings.bugReport
        if (bugRep == null) {
            bugReport = this
            create(projectID)
        } else {
            if (bugRep.bugReportSettings!!.win.windowStage.stage.isShowing) {
                bugRep.bugReportSettings!!.win.windowStage.stage.requestFocus()
            } else {
                bugReport = this
                create(projectID)
            }
        }
    }

    private fun create(projectID: Int) {
        val bugCfgStage = Stage()
        bugCfgStage.title = "Bug Report"
        bugCfgStage.icons.add(ImageLoader.getAppIconSmall())
        bugCfgStage.isResizable = false
        bugCfgStage.width = 720.0
        bugCfgStage.height = 540.0

        val borderpane = BorderPane()
        val contentNode = getLayout(projectID)

        // The UI (Client Area) to display
        borderpane.center = contentNode

        bugReportSettings = WindowScene(WindowStage("bugconfig", bugCfgStage), borderpane, 1)

        bugCfgStage.scene = bugReportSettings
        bugCfgStage.show()

        //if (DebugHelper.isDebugVersion()) { DebugHelper.debugBugTemplate(); }

        when (projectID) {
            -1 -> {
                accountTP!!.isVisible = false
                accountCB.isVisible = false
            }

            else -> {
                AppConfiguration.accountList.clear()
                try {
                    AccountXmlFactory.readAccountNameFromXML(projectID)
                } catch (e1: MalformedURLException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                }

            }
        }

        versionTF!!.text = AppConfiguration.found_in_version
        serverCB.value = AppConfiguration.server[AppConfiguration.srvSel]
        locaCB.value = AppConfiguration.loca[AppConfiguration.locaSel]

        //parseBugTemplate();
        populateBugTemplate(projectID)
    }


    private fun getLayout(pjState: Int): Node {

        val mainContent = BorderPane()
        mainContent.styleClass.add("background")

        val mainGrid = GridPane()
        val cfgGrid = GridPane()
        val prevGrid = GridPane()
        val cfgInnerGrid = GridPane()
        val controls = HBox()

        cfgGrid.hgap = 30.0
        cfgGrid.padding = Insets(10.0, 10.0, 0.0, 10.0)  //padding top, left, bottom, right
        cfgGrid.alignment = Pos.BASELINE_CENTER

        prevGrid.padding = Insets(10.0, 10.0, 0.0, 10.0)  //padding top, left, bottom, right
        cfgInnerGrid.hgap = 10.0
        cfgInnerGrid.vgap = 15.0

        //add components
        val tmplCfg = TitledPane(LanguageController.getString("settings"), cfgInnerGrid)
        tmplCfg.isCollapsible = false
        GridPane.setConstraints(tmplCfg, 0, 0)
        cfgGrid.children.add(tmplCfg)

        val browserTP = Label("Browser")
        GridPane.setConstraints(browserTP, 0, 0)
        cfgInnerGrid.children.add(browserTP)

        browserTF = TextField(AppConfiguration.browser)
        GridPane.setConstraints(browserTF, 1, 0)
        cfgInnerGrid.children.add(browserTF)

        val versionTP = Label("Version")
        GridPane.setConstraints(versionTP, 0, 1)
        cfgInnerGrid.children.add(versionTP)

        versionTF = TextField()
        GridPane.setConstraints(versionTF, 1, 1)
        cfgInnerGrid.children.add(versionTF)

        val serverTP = Label("Server")
        GridPane.setConstraints(serverTP, 0, 2)
        cfgInnerGrid.children.add(serverTP)

        serverCB = ComboBox(AppConfiguration.server)
        GridPane.setConstraints(serverCB, 1, 2)
        cfgInnerGrid.children.add(serverCB)

        val langTP = Label(LanguageController.getString("language"))
        GridPane.setConstraints(langTP, 0, 3)
        langTP.prefWidth = 95.0
        cfgInnerGrid.children.add(langTP)

        locaCB = ComboBox(AppConfiguration.loca)
        GridPane.setConstraints(locaCB, 1, 3)
        cfgInnerGrid.children.add(locaCB)

        accountTP = Label("Account")
        GridPane.setConstraints(accountTP, 0, 4)
        cfgInnerGrid.children.add(accountTP)

        accountCB = ComboBox()
        GridPane.setConstraints(accountCB, 1, 4)
        cfgInnerGrid.children.add(accountCB)

        tmplTA = TextArea()
        tmplTA!!.isEditable = false
        tmplTA!!.minHeight = 320.0
        tmplTA!!.style = "-fx-font-size: 12pt;"

        val tmplPrev = TitledPane("Template " + LanguageController.getString("preview"), tmplTA)
        tmplPrev.isCollapsible = false
        GridPane.setConstraints(tmplPrev, 0, 0)
        prevGrid.children.add(tmplPrev)

        val status = Label()
        val saveCfg = Button(LanguageController.getString("saveCfg"))
        val copyTemplate = Button(LanguageController.getString("template_copy"))
        controls.children.addAll(status, saveCfg, copyTemplate)

        controls.padding = Insets(15.0, 12.0, 15.0, 12.0)  //padding top, left, bottom, right
        controls.spacing = 10.0
        controls.alignment = Pos.CENTER_RIGHT

        val cfg = ColumnConstraints()
        cfg.percentWidth = 45.0
        val prev = ColumnConstraints()
        prev.percentWidth = 55.0
        mainGrid.columnConstraints.addAll(cfg, prev)

        GridPane.setConstraints(cfgGrid, 0, 0)
        GridPane.setConstraints(prevGrid, 1, 0)
        mainGrid.children.add(cfgGrid)
        mainGrid.children.add(prevGrid)

        //add to borderpane layout
        mainContent.center = mainGrid
        mainContent.bottom = controls

        saveCfg.setOnAction { event ->
            AppConfiguration.browser = browserTF.text
            AppConfiguration.found_in_version = versionTF!!.text
            AppConfiguration.srvSel = serverCB.selectionModel.selectedIndex
            AppConfiguration.locaSel = locaCB.selectionModel.selectedIndex
            if (pjState >= 0) {
                AppConfiguration.projectData[pjState].setProjectAccBookmarkIdx(accountCB.selectionModel.selectedIndex)
                if (accountCB.value != null) {
                    AppConfiguration.projectData[pjState].setProjectAccBookmarkName(accountCB.selectionModel.selectedItem)
                }
            }

            //            try {
            //                SettingsXmlFactory.saveBugTemplateSettings(pjState);
            //                populateBugTemplate(pjState);
            //                status.setStyle("-fx-text-fill: #55c4fe;"); status.setText(LanguageController.getString("cfgSaved"));
            //            } catch (MalformedURLException | FileNotFoundException e1) {
            //                if (AppConfiguration.debugVersion) { e1.printStackTrace(); } else { new ExceptionHandler(Thread.currentThread(), e1); }
            //            }
        }

        copyTemplate.setOnAction { event ->
            AppConfiguration.setStringToClipboard(tmplTA!!.text)
            status.style = "-fx-text-fill: #55c4fe;"
            status.text = LanguageController.getString("template_copied")
        }

        browserTF.setOnKeyReleased { ke -> populateBugTemplate(pjState) }

        versionTF!!.setOnKeyReleased { ke -> populateBugTemplate(pjState) }

        accountCB.valueProperty().addListener { ov, t, t1 ->
            if (t1 != null) {
                AppConfiguration.projectData[pjState].setProjectAccBookmarkName(accountCB.selectionModel.selectedItem)
                populateBugTemplate(pjState)
            }
        }

        serverCB.valueProperty().addListener { ov, t, t1 ->
            AppConfiguration.srvSel = serverCB.selectionModel.selectedIndex
            if (t1 != null) {
                populateBugTemplate(pjState)
            }
        }

        locaCB.valueProperty().addListener { ov, t, t1 ->
            AppConfiguration.locaSel = locaCB.selectionModel.selectedIndex
            if (t1 != null) {
                populateBugTemplate(pjState)
            }
        }

        return mainContent
    }

    private fun populateBugTemplate(projectInt: Int) {
        tmplTA!!.text = getBugtemplate(projectInt)
    }

    /**
     * returns bug template
     * only called in BugReportSettings scene
     */
    private fun getBugtemplate(projectInt: Int): String {
        var accName: String
        var orAny: String
        try {
            if (AppConfiguration.projectData[projectInt].getProjectAccBookmarkName() == "") {
                accName = ""
                orAny = ""
            } else if (AppConfiguration.projectData[projectInt].getProjectAccBookmarkName() == LanguageController.getString("xml_not_found")) {
                accName = ""
                orAny = ""
            } else if (AppConfiguration.projectData[projectInt].getProjectAccBookmarkName() == LanguageController.getString("no_account_found")) {
                accName = ""
                orAny = ""
            } else {
                accName = AppConfiguration.projectData[projectInt].getProjectAccBookmarkName()
                orAny = AppConfiguration.bugtemplate[11]
            }
        } catch (e1: ArrayIndexOutOfBoundsException) {
            accName = ""
            orAny = ""
        }

        return AppConfiguration.bugtemplate[0] + browserTF.text + "\n" +
                AppConfiguration.bugtemplate[1] + versionTF!!.text + "\n" +
                AppConfiguration.bugtemplate[2] + serverCB.value + "\n" +
                AppConfiguration.bugtemplate[3] + CommonUtil.getTime("HH:mm") + "\n" +
                AppConfiguration.bugtemplate[4] + accName + orAny + "\n" +
                AppConfiguration.bugtemplate[5] + locaCB.value + AppConfiguration.bugtemplate[11] + "\n" +
                AppConfiguration.bugtemplate[6] + "\n\n" +
                AppConfiguration.bugtemplate[7] + "\n" +
                AppConfiguration.bugtemplate[8] + "\n\n" +
                AppConfiguration.bugtemplate[9] + "\n" +
                AppConfiguration.bugtemplate[8] + "\n\n" +
                AppConfiguration.bugtemplate[10] + "\n" +
                AppConfiguration.bugtemplate[8]
    }

    companion object {
        var bugReport: BugReportSettings? = null
        lateinit var browserTF: TextField
        lateinit var accountCB: ComboBox<String>
        lateinit var serverCB: ComboBox<String>
        lateinit var locaCB: ComboBox<String>

        fun copyTemplate() {
            val projectID = AppConfiguration.pjState

            if (projectID == -1) {
                MessageDialog(0, LanguageController.getString("project_not_selected"), 350, 220)
            } else {
                try {
                    AccountXmlFactory.getFavAccName(projectID)
                    AppConfiguration.setStringToClipboard(AppConfiguration.onCopyBugtemplate(projectID))
                    MainLayout.status.text = LanguageController.getString("template_copied")
                } catch (e1: Exception) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                }

            }
        }
    }
}