package net.rickiekarp.botter.view

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.util.parser.JsonParser
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.view.ChangelogScene
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.core.view.SettingsScene
import net.rickiekarp.botlib.BotConfig
import net.rickiekarp.botlib.BotLauncher
import net.rickiekarp.botlib.PluginConfig
import net.rickiekarp.botlib.enums.BotPlatforms
import net.rickiekarp.botlib.enums.BotType
import net.rickiekarp.botlib.model.Credentials
import net.rickiekarp.botlib.model.PluginData
import net.rickiekarp.botlib.plugin.BotSetting
import net.rickiekarp.botlib.plugin.PluginExecutor
import net.rickiekarp.botter.botservice.BotTask
import net.rickiekarp.botter.listcell.FoldableListCell
import net.rickiekarp.botter.settings.AppConfiguration
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import net.rickiekarp.core.view.layout.AppLayout
import org.json.JSONArray
import org.json.JSONObject

import java.io.File
import java.util.Base64

class MainLayout : AppLayout {
    private val optionBox: HBox
    private val modCBox: ComboBox<PluginData>
    private val runBtn: Button
    private val stopBtn: Button
    private var status: Label? = null
    private val timeRemainL: Label
    private val timeBox: VBox
    private var loadBar: ProgressIndicator? = null
    private var listView: ListView<BotSetting>? = null

    private var botTask: BotTask? = null
    private var botLauncher: BotLauncher? = null

    val launchNode: Node
        get() = optionBox

    private val controlsNode: Node
        get() {
            val node = AnchorPane()
            node.minHeight = 50.0

            val controls = HBox(5.0)

            val statusBox = HBox(10.0)
            statusBox.alignment = Pos.BOTTOM_RIGHT

            status = Label()

            loadBar = ProgressIndicator()
            loadBar!!.maxHeight = 20.0
            loadBar!!.maxWidth = 20.0
            loadBar!!.progress = ProgressIndicator.INDETERMINATE_PROGRESS
            loadBar!!.isVisible = false

            statusBox.children.addAll(loadBar, status)

            AnchorPane.setLeftAnchor(controls, 10.0)
            AnchorPane.setBottomAnchor(controls, 5.0)
            AnchorPane.setRightAnchor(statusBox, 15.0)
            AnchorPane.setBottomAnchor(statusBox, 10.0)

            if (DebugHelper.DEBUGVERSION) {
                statusBox.style = "-fx-background-color: blue"
                controls.style = "-fx-background-color: black"
            }

            node.children.addAll(controls, statusBox)
            return node
        }

    private//SETTINGS LIST
    val settingsNode: ListView<BotSetting>
        get() {
            listView = ListView()
            PluginConfig.settingsList = FXCollections.observableArrayList()
            PluginConfig.settingsList!!.add(
                    BotSetting.Builder.create().setName(LanguageController.getString("option_timer")).setDescription(LanguageController.getString("option_timer_desc")).setVisible(true).setNode(timerSection).build()
            )
            listView!!.items = PluginConfig.settingsList

            listView!!.setCellFactory { lv -> FoldableListCell(listView!!) }

            if (DebugHelper.DEBUGVERSION) {
                listView!!.style = "-fx-background-color: gray"
            }
            return listView!!
        }


    val generalSection: VBox
        get() {

            val content = VBox()
            content.spacing = 5.0

            val btn_settings = Button()
            btn_settings.tooltip = Tooltip(LanguageController.getString("settings"))
            btn_settings.styleClass.add("decoration-button-settings")

            val btn_about = Button()
            btn_about.tooltip = Tooltip(LanguageController.getString("changelog"))
            btn_about.styleClass.add("decoration-button-about")

            btn_settings.setOnAction { event -> SettingsScene() }
            btn_about.setOnAction { event -> ChangelogScene() }

            val hbox = HBox()
            hbox.padding = Insets(0.0, 0.0, 0.0, listView!!.width / 4)
            hbox.spacing = 5.0
            hbox.children.addAll(btn_settings, btn_about)

            content.children.addAll(hbox)
            return content
        }

    private val timerSection: VBox
        get() {
            val content = VBox()
            content.spacing = 5.0

            val periodCheck = CheckBox(LanguageController.getString("setPeriodicalRun"))
            periodCheck.style = "-fx-font-size: 10pt;"

            val periods = ComboBox<String>()
            periods.items.addAll("30 minutes", "45 minutes", "60 minutes")
            periods.value = AppConfiguration.runInterval.toString() + " minutes"
            periods.valueProperty().addListener { ov, t, t1 ->
                when (periods.selectionModel.selectedIndex) {
                    0 -> AppConfiguration.runInterval = 30
                    1 -> AppConfiguration.runInterval = 45
                    2 -> AppConfiguration.runInterval = 60
                }
            }

            periodCheck.setOnAction { event ->
                if (periodCheck.isSelected) {
                    content.children.add(periods)
                    AppConfiguration.canBotRunPeriodical = true
                } else {
                    content.children.remove(periods)
                    AppConfiguration.canBotRunPeriodical = false
                }
            }

            content.children.addAll(periodCheck)
            return content
        }

    private//ActionListener
            /*RandomStringUtils.randomAlphanumeric(16) +*///if the plugin json can not be found earlier, add a new entry
    val credentialsSection: VBox
        get() {
            LogFileHandler.logger.info("Loading credentials section")
            val content = VBox()
            content.spacing = 5.0

            val loginLabel = Label(LanguageController.getString("login"))
            val passwordLabel = Label(LanguageController.getString("password"))
            val loginTF = TextField()
            val passTF = PasswordField()
            val saveButton = Button(LanguageController.getString("saveCfg"))
            val deleteButton = Button(LanguageController.getString("remove"))

            content.children.addAll(loginLabel, loginTF, passwordLabel, passTF)

            val deviceJson = arrayOf(JsonParser.readJsonFromFile(File(Configuration.config.configDirFile.toString() + File.separator + "plugins" + File.separator + "credentials.json")))
            if (deviceJson[0] != null) {
                val jsonArray = deviceJson[0].getJSONArray("credentials")
                var cJson: JSONObject
                for (i in 0 until jsonArray.length()) {
                    cJson = jsonArray.getJSONObject(i)
                    if (cJson.getString("primaryKey") == modCBox.selectionModel.selectedItem.pluginName.get()) {
                        loginTF.text = cJson.getString("login")

                        val encodedPass = cJson.getString("password")
                        val trimmedPass = encodedPass.substring(16)
                        val bytes: ByteArray
                        try {
                            bytes = trimmedPass.toByteArray(charset("UTF-8"))
                            val decoded = Base64.getDecoder().decode(bytes)
                            val decodedString = String(decoded)
                            passTF.text = decodedString
                            modCBox.selectionModel.selectedItem.pluginCredentials = Credentials(loginTF.text, decodedString)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        content.children.add(deleteButton)
                        break
                    }
                }
            }

            deleteButton.setOnAction { event ->
                val jsonArray = deviceJson[0].getJSONArray("credentials")
                for (i in 0 until jsonArray.length()) {
                    if (jsonArray.getJSONObject(i).getString("primaryKey") == modCBox.selectionModel.selectedItem.pluginName.get()) {
                        jsonArray.remove(i)
                        JsonParser.writeJsonObjectToFile(deviceJson[0], File(Configuration.config.configDirFile.toString() + File.separator + "plugins"), "credentials.json")
                        break
                    }
                }
                modCBox.selectionModel.selectedItem.pluginCredentials = null
                loginTF.clear()
                passTF.clear()
                content.children.remove(deleteButton)
                content.children.remove(saveButton)
            }
            loginTF.setOnKeyReleased { event ->
                if (!content.children.contains(saveButton)) {
                    content.children.add(saveButton)
                }
            }
            passTF.setOnKeyReleased { event ->
                if (!content.children.contains(saveButton)) {
                    content.children.add(saveButton)
                }
            }

            saveButton.setOnAction { event ->
                if (loginTF.text.isEmpty() || passTF.text.isEmpty()) {
                    MessageDialog(1, "Please enter your full login details!", 450, 200)
                } else {
                    val jsonArray: JSONArray
                    val login = loginTF.text
                    val pass = Base64.getEncoder().encodeToString(passTF.text.toByteArray())
                    if (deviceJson[0] == null) {
                        val newDeviceJson = JSONObject()
                        jsonArray = JSONArray()
                        newDeviceJson.put("credentials", jsonArray)

                        val credentialsJson = JSONObject()
                        credentialsJson.put("primaryKey", modCBox.selectionModel.selectedItem.pluginName)
                        credentialsJson.put("login", login)
                        credentialsJson.put("password", pass)
                        jsonArray.put(credentialsJson)

                        JsonParser.writeJsonObjectToFile(newDeviceJson, File(Configuration.config.configDirFile.toString() + File.separator + "plugins"), "credentials.json")
                        deviceJson[0] = newDeviceJson
                        content.children.remove(saveButton)
                    } else {
                        var addEntry = true
                        jsonArray = deviceJson[0].getJSONArray("credentials")
                        var currentCredentials: JSONObject
                        for (i in 0 until jsonArray.length()) {
                            currentCredentials = jsonArray.getJSONObject(i)
                            if (jsonArray.getJSONObject(i).getString("primaryKey") == modCBox.selectionModel.selectedItem.pluginName.get()) {
                                currentCredentials.put("login", login)
                                currentCredentials.put("password", pass)
                                addEntry = false
                                break
                            }
                        }

                        if (addEntry) {
                            val credentialsJson = JSONObject()
                            credentialsJson.put("primaryKey", modCBox.selectionModel.selectedItem.pluginName)
                            credentialsJson.put("login", login)
                            credentialsJson.put("password", pass)
                            jsonArray.put(credentialsJson)
                        }

                        JsonParser.writeJsonObjectToFile(deviceJson[0], File(Configuration.config.configDirFile.toString() + File.separator + "plugins"), "credentials.json")
                    }

                    modCBox.selectionModel.selectedItem.pluginCredentials = Credentials(loginTF.text, passTF.text)
                    setStatus("neutral", LanguageController.getString("browser_info_updated"))
                    content.children.remove(saveButton)
                    if (!content.children.contains(deleteButton)) {
                        content.children.add(deleteButton)
                    }
                }
            }

            return content
        }

    private//ActionListener
    val browserSelectionSection: HBox
        get() {
            val content = HBox()
            content.spacing = 10.0

            val browserSelector = ComboBox<BotType.Bot>()
            browserSelector.prefWidth = 120.0
            browserSelector.promptText = "NONE"
            browserSelector.style = "-fx-font-size: 10pt;"

            for (bot in BotType.Bot.values()) {
                if (bot.botPlatform == BotPlatforms.WEB) {
                    browserSelector.items.add(bot)
                }
            }

            val deviceJson = arrayOf(JsonParser.readJsonFromFile(File(BotConfig.modulesDirFile.toString() + File.separator + "devices" + File.separator + modCBox.selectionModel.selectedItem.pluginType!!.getDisplayableType().toLowerCase() + ".json")))
            if (deviceJson[0] != null) {
                for (bot in BotType.Bot.values()) {
                    if (BotType.Bot.valueOf(deviceJson[0].getString("browser")) == bot && bot.botPlatform == BotPlatforms.WEB) {
                        browserSelector.selectionModel.select(bot)
                        PluginConfig.botType = bot
                        break
                    }
                }
            }

            val saveButton = Button(LanguageController.getString("saveCfg"))
            saveButton.style = "-fx-font-size: 10pt;"
            saveButton.isVisible = false
            browserSelector.valueProperty().addListener { ov, t, t1 ->
                PluginConfig.botType = t1
                saveButton.isVisible = true

            }

            saveButton.setOnAction { event ->
                if (deviceJson[0] == null) {
                    deviceJson[0] = JSONObject()
                    deviceJson[0].put("browser", browserSelector.selectionModel.selectedItem)
                } else {
                    deviceJson[0].put("browser", browserSelector.selectionModel.selectedItem)
                }

                JsonParser.writeJsonObjectToFile(deviceJson[0], File(BotConfig.modulesDirFile.toString() + File.separator + "devices"), modCBox.selectionModel.selectedItem.pluginType!!.getDisplayableType().toLowerCase() + ".json")
                setStatus("neutral", LanguageController.getString("device_info_updated"))
                saveButton.isVisible = false
            }

            content.children.addAll(browserSelector, saveButton)
            return content
        }

    private//also update the current device info variables
    //ActionListener
    val androidDeviceSelection: VBox
        get() {
            LogFileHandler.logger.info("Loading android device selection")
            val content = VBox()
            content.spacing = 5.0

            val nameLabel = Label(LanguageController.getString("devicename"))
            val verLabel = Label(LanguageController.getString("deviceversion"))
            val serialLabel = Label(LanguageController.getString("deviceserial"))
            val nameTF = TextField()
            val verTF = TextField()
            val deviceSerialTF = TextField()

            val deviceJson = arrayOf(JsonParser.readJsonFromFile(File(BotConfig.modulesDirFile.toString() + File.separator + "devices" + File.separator + modCBox.selectionModel.selectedItem.pluginType!!.getDisplayableType().toLowerCase() + ".json")))
            if (deviceJson[0] != null) {
                nameTF.text = deviceJson[0].getJSONObject("1").getString("name")
                verTF.text = deviceJson[0].getJSONObject("1").getString("version")
                deviceSerialTF.text = deviceJson[0].getJSONObject("1").getString("serial")
                updateAndroidDeviceInfo(nameTF.text, verTF.text, deviceSerialTF.text)
            }

            val saveButton = Button(LanguageController.getString("saveCfg"))
            saveButton.isVisible = false
            nameTF.setOnKeyReleased { event -> saveButton.isVisible = true }
            verTF.setOnKeyReleased { event -> saveButton.isVisible = true }
            deviceSerialTF.setOnKeyReleased { event -> saveButton.isVisible = true }


            saveButton.setOnAction { event ->
                if (deviceJson[0] == null) {
                    deviceJson[0] = JSONObject()
                    val deviceObj = JSONObject()
                    deviceObj.put("name", nameTF.text)
                    deviceObj.put("version", verTF.text)
                    deviceObj.put("serial", deviceSerialTF.text)
                    deviceJson[0].put("1", deviceObj)
                } else {
                    deviceJson[0].getJSONObject("1").put("name", nameTF.text)
                    deviceJson[0].getJSONObject("1").put("version", verTF.text)
                    deviceJson[0].getJSONObject("1").put("serial", deviceSerialTF.text)
                }

                JsonParser.writeJsonObjectToFile(deviceJson[0], File(BotConfig.modulesDirFile.toString() + File.separator + "devices"), modCBox.selectionModel.selectedItem.pluginType!!.getDisplayableType().toLowerCase() + ".json")
                updateAndroidDeviceInfo(nameTF.text, verTF.text, deviceSerialTF.text)
                setStatus("neutral", LanguageController.getString("browser_info_updated"))
                saveButton.isVisible = false
            }

            updateAndroidDeviceInfo(nameTF.text, verTF.text, deviceSerialTF.text)

            content.children.addAll(nameLabel, nameTF, verLabel, verTF, serialLabel, deviceSerialTF, saveButton)
            return content
        }

    override val layout: Node
        get() = controlsNode

    fun clearModules() {
        modCBox.items.clear()
    }

    init {
        mainLayout = this

        optionBox = HBox(10.0)
        optionBox.alignment = Pos.BOTTOM_LEFT
        optionBox.padding = Insets(5.0, 0.0, 5.0, 5.0)

        val moduleBox = VBox()
        val asgmBox = VBox()

        modCBox = ComboBox()
        modCBox.promptText = "none"
        modCBox.minWidth = 175.0
        //        for (int i = 0; i < PluginData.pluginData.size(); i++) {
        //            modCBox.getItems().add(PluginData.pluginData.get(i));
        //        }

        //selected value showed in combo box
        modCBox.setConverter(object : StringConverter<PluginData>() {
            override fun toString(plugin: PluginData?): String? {
                return plugin?.pluginName!!.get()
            }

            override fun fromString(plugin: String): PluginData {
                return PluginData(null, plugin, null, null, null)
            }
        })

//        PluginData.pluginData.addListener({ change ->
//            change.next()
//            if (change.wasAdded()) {
//                if (PluginData.pluginData[change.getFrom()].pluginOldVersion != null) {
//                    //add plugin to module combobox if it doesn't exist already
//                    if (!modCBox.items.contains(PluginData.pluginData[change.getFrom()])) {
//                        modCBox.items.add(PluginData.pluginData[change.getFrom()])
//                    }
//                }
//            }
//        } as ListChangeListener<PluginData>)


        runBtn = Button("Run")
        runBtn.isDisable = true

        stopBtn = Button("Stop")
        stopBtn.isDisable = true


        timeBox = VBox()
        timeBox.isVisible = false
        timeBox.alignment = Pos.BOTTOM_CENTER
        timeBox.padding = Insets(0.0, 0.0, 0.0, 15.0)

        val timeL = Label("Time until next run:")
        timeL.style = "-fx-font-size: 9pt;"
        timeRemainL = Label("00:00:00")
        timeRemainL.style = "-fx-font-size: 9pt;"

        timeBox.children.addAll(timeL, timeRemainL)

        moduleBox.children.add(modCBox)
        optionBox.children.addAll(moduleBox, asgmBox, runBtn, stopBtn, timeBox)

        //ActionListener
        modCBox.valueProperty().addListener { ov, t, t1 ->
            if (t == null) {
                runBtn.isDisable = false
                MainScene.mainScene.borderPane.right = settingsNode
                MainScene.mainScene.borderPane.bottom = controlsNode
            }

            PluginConfig.settingsList!!.clear()
            PluginConfig.settingsList!!.add(
                    BotSetting.Builder.create().setName(LanguageController.getString("option_timer")).setDescription(LanguageController.getString("option_timer_desc")).setVisible(true).setNode(timerSection).build()
            )

            when (modCBox.selectionModel.selectedItem.pluginType) {
                BotPlatforms.ANDROID -> {
                    PluginConfig.botType = BotType.Bot.ANDROID
                    PluginConfig.settingsList!!.add(
                            BotSetting.Builder.create().setName(LanguageController.getString("androidSelect")).setDescription(LanguageController.getString("androidSelect_desc")).setVisible(true).setNode(androidDeviceSelection).build()
                    )
                }
                BotPlatforms.WEB -> {
                    PluginConfig.settingsList!!.add(
                            BotSetting.Builder.create().setName(LanguageController.getString("credentialsSelect")).setDescription(LanguageController.getString("credentialsSelect_desc")).setVisible(true).setNode(credentialsSection).build()
                    )
                    PluginConfig.settingsList!!.add(
                            BotSetting.Builder.create().setName(LanguageController.getString("browserSelect")).setDescription(LanguageController.getString("browserSelect_desc")).setVisible(true).setNode(browserSelectionSection).build()
                    )
                }
            }

            PluginConfig.botPlatform = modCBox.selectionModel.selectedItem.pluginType
            botLauncher = BotLauncher()
            botLauncher!!.createBotRunner(modCBox.selectionModel.selectedItem)

            try {
                PluginExecutor.executeLayoutSetter(BotLauncher.runnerInstance!!, modCBox.selectionModel.selectedItem)
            } catch (e: Exception) {
                if (DebugHelper.DEBUGVERSION) {
                    e.printStackTrace()
                } else {
                    ExceptionHandler(Thread.currentThread(), e)
                }
            }
        }

        runBtn.setOnAction { event ->
            setStatus("neutral", "Loading bot...")
            setLoadBarVisible(true)
            switchMode()
            MainScene.mainScene.borderPane.center = null

            //remove all buttons from control node
            val pane = MainScene.mainScene.borderPane.bottom as AnchorPane
            val controls = pane.children[0] as HBox
            for (size in controls.children.size - 1 downTo 0) {
                controls.children.removeAt(size)
            }

            if (botTask != null) {
                botTask!!.resetTimer()
            }
            botTask = BotTask(botLauncher!!, modCBox.selectionModel.selectedItem)
        }

        stopBtn.setOnAction { event ->
            setStatus("neutral", "Stopping...")
            botTask!!.cancel()
        }

        if (DebugHelper.DEBUGVERSION) {
            optionBox.style = "-fx-background-color: darkgray"
            moduleBox.style = "-fx-background-color: yellow"
            moduleBox.style = "-fx-background-color: green"
            timeBox.style = "-fx-background-color: darkblue"
        }
    }

    fun switchMode() {
        modCBox.isDisable = !modCBox.isDisabled
        runBtn.isDisable = !runBtn.isDisabled
        stopBtn.isDisable = !stopBtn.isDisabled
    }

    fun switchTimeBox() {
        timeBox.isVisible = !timeBox.isVisible
    }

    fun loadBot() {
        runBtn.fire()
    }

    fun setStatus(type: String, text: String) {
        when (type) {
            "success" -> status!!.style = "-fx-text-fill: #55c4fe;"
            "fail" -> status!!.style = "-fx-text-fill: red;"
            "neutral" -> status!!.style = "-fx-text-fill: white;"
        }
        status!!.text = text
    }

    fun updateCountdown(text: String) {
        timeRemainL.text = text
    }

    fun setLoadBarVisible(bool: Boolean) {
        loadBar!!.isVisible = bool
    }

    private fun updateAndroidDeviceInfo(name: String, version: String, udid: String) {
        BotConfig.DEVICE_NAME = name
        BotConfig.VERSION = version
        BotConfig.UDID = udid
    }

    override fun postInit() {

    }

    companion object {
        var mainLayout: MainLayout? = null

        val instance: MainLayout
            get() {
                if (mainLayout == null) {
                    mainLayout = MainLayout()
                }
                return mainLayout!!
            }
    }
}

