package net.rickiekarp.botter.view

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.util.parser.JsonParser
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.botlib.BotConfig
import net.rickiekarp.botlib.PluginConfig
import net.rickiekarp.botlib.enums.BotType
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.stage.DirectoryChooser
import org.json.JSONObject

import java.io.File
import java.util.logging.Level
import kotlin.math.max
import kotlin.math.min

class BotSetupLayout {
    private val supportedBrowsers = arrayOf("chrome", "firefox")
    private var browserSetupStep = 1
    private var messageLabel: Label? = null
    private var setupGrid: GridPane? = null
    private var prevButton: Button? = null
    private var nextButton: Button? = null

    private val minTileSize = 50.0
    private val nColumns = supportedBrowsers.size.toDouble()
    private val nRows = 1.0
    private val prefTileSize = SimpleDoubleProperty(minTileSize)

    val layout: Node
        get() {
            val borderpane = BorderPane()

            val vbox = VBox()
            vbox.alignment = Pos.BOTTOM_CENTER

            messageLabel = Label()
            messageLabel!!.padding = Insets(30.0, 0.0, 0.0, 0.0)
            setupGrid = GridPane()
            setupGrid!!.alignment = Pos.CENTER
            setupGrid!!.stylesheets.add("ui/icons/browser/LogoStyle.css")

            VBox.setVgrow(setupGrid, Priority.ALWAYS)
            vbox.children.addAll(messageLabel, setupGrid)
            val controls = HBox()
            controls.padding = Insets(10.0, 12.0, 10.0, 12.0)
            controls.spacing = 10.0
            controls.alignment = Pos.CENTER_RIGHT

            prevButton = Button(LanguageController.getString("back"))
            prevButton!!.isVisible = false
            prevButton!!.setOnAction {
                setupGrid!!.children.clear()
                browserSetupStep--
                showSetupStep(browserSetupStep)
            }
            controls.children.add(prevButton)

            nextButton = Button(LanguageController.getString("next"))
            nextButton!!.setOnAction {
                setupGrid!!.children.clear()
                browserSetupStep++
                if (browserSetupStep == 4 && PluginConfig.botType === BotType.Bot.FIREFOX) {
                    browserSetupStep++
                }
                showSetupStep(browserSetupStep)
            }
            controls.children.add(nextButton)

            showSetupStep(browserSetupStep)
            borderpane.center = vbox
            borderpane.bottom = controls

            setupGrid!!.layoutBoundsProperty().addListener { _, _, newBounds ->
                prefTileSize.set(max(minTileSize, min(newBounds.width / nColumns, newBounds.height / nRows)))
                calculateSizes()
            }

            if (DebugHelper.isDebugVersion) {
                setupGrid!!.style = "-fx-background-color: gray;"
            }

            LogFileHandler.logger.log(Level.INFO, "open.BotSetupDialog")
            return borderpane
        }

    private fun showSetupStep(step: Int) {
        when (step) {

            //set nodejs path
            1 -> {
                messageLabel!!.text = LanguageController.getString("desc_setup_1_0")
                nextButton!!.isDisable = true

                val descSetup41 = Label(LanguageController.getString("desc_setup_1_1"))
                descSetup41.alignment = Pos.CENTER
                descSetup41.maxHeight = 100.0
                descSetup41.isWrapText = true
                GridPane.setConstraints(descSetup41, 0, 0)

                val searchNode = Button(LanguageController.getString("search"))
                searchNode.maxHeight = 100.0
                searchNode.alignment = Pos.CENTER
                searchNode.setOnAction {
                    val directoryChooser = DirectoryChooser()
                    directoryChooser.initialDirectory = Configuration.config.jarFile.parentFile
                    val selectedDirectory = directoryChooser.showDialog(MainScene.mainScene.windowScene!!.window)

                    if (selectedDirectory != null) {
                        setupGrid!!.children.clear()
                        val text = Label(selectedDirectory.path)
                        GridPane.setConstraints(text, 0, 0)
                        setupGrid!!.children.add(text)
                        BotConfig.nodeBinary = selectedDirectory.path
                        nextButton!!.isDisable = false
                    }
                }
                GridPane.setConstraints(searchNode, 0, 1)

                setupGrid!!.children.addAll(descSetup41, searchNode)
                calculateSizes()
            }

            //select browser
            2 -> {
                messageLabel!!.text = LanguageController.getString("desc_setup_2_0")
                prevButton!!.isVisible = true
                nextButton!!.isDisable = true

                // create n button controls for ui.icons.browser selection
                val group = ToggleGroup()
                var i = 1
                while (i <= nColumns) {
                    // create a ToggleButton for choosing a browser.
                    val browserChoice = ToggleButton()
                    browserChoice.toggleGroup = group
                    browserChoice.userData = i
                    browserChoice.styleClass.add(getBrowserStyle(i))

                    // position the button in the grid.
                    GridPane.setConstraints(browserChoice, i, 0)
                    browserChoice.setMinSize(minTileSize, minTileSize)
                    browserChoice.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
                    setupGrid!!.children.add(browserChoice)
                    i++
                }

                group.selectedToggleProperty().addListener { _, _, _ ->
                    if (group.selectedToggle != null) {
                        when (group.selectedToggle.userData as Int) {
                            1 -> PluginConfig.botType = BotType.Bot.CHROME
                            2 -> PluginConfig.botType = BotType.Bot.FIREFOX
                        }
                        nextButton!!.isDisable = false
                    } else {
                        PluginConfig.botType = BotType.Bot.NONE
                        nextButton!!.isDisable = true
                    }
                }
                calculateSizes()
            }

            //            //select driver location
            //            case 2:
            //                messageLabel.setText(LanguageController.getString("desc_setup_2_0"));
            //                prevButton.setVisible(true);
            //                nextButton.setDisable(true);
            //
            //                // create a ToggleButton for choosing a browser.
            //                final Button browserChoice = new Button(LanguageController.getString("search"));
            //                browserChoice.setOnAction(event -> {
            //                    DirectoryChooser directoryChooser = new DirectoryChooser();
            //                    directoryChooser.setInitialDirectory(BotConfig.getModulesDirFile());
            //                    File selectedDirectory = directoryChooser.showDialog(MainScene.mainScene.getWindowScene().getWindow());
            //
            //                    if (selectedDirectory != null) {
            //                        setupGrid.getChildren().clear();
            //                        Label text = new Label(selectedDirectory.getPath());
            //                        GridPane.setConstraints(text, 0, 0);
            //                        setupGrid.getChildren().add(text);
            //                        PluginConfig.driverPath = selectedDirectory.getPath();
            //                        nextButton.setDisable(false);
            //                    }
            //                });
            //
            //                final Button browserChoice1 = new Button(LanguageController.getString("download"));
            //                browserChoice1.setOnAction(event -> {
            //                    try {
            //                        switch (PluginConfig.botType) {
            //                            case CHROME: Desktop.getDesktop().browse(new URI(driverDownload[0])); break;
            //                            case FIREFOX: Desktop.getDesktop().browse(new URI(driverDownload[1])); break;
            //                        }
            //                    } catch (IOException | URISyntaxException e1) {
            //                        e1.printStackTrace();
            //                    }
            //                });
            //
            //                Label test = new Label(LanguageController.getString("desc_setup_2_1"));
            //                test.setAlignment(Pos.CENTER);
            //                GridPane.setConstraints(test, 0, 0);
            //
            //                Label test1 = new Label(LanguageController.getString("desc_setup_2_2"));
            //                test1.setAlignment(Pos.CENTER);
            //                GridPane.setConstraints(test1, 1, 0);
            //
            //
            //                // position the button in the grid.
            //                GridPane.setConstraints(browserChoice, 0, 1);
            //                GridPane.setConstraints(browserChoice1, 1, 1);
            //
            //                browserChoice.setMinSize(MIN_TILE_SIZE, MIN_TILE_SIZE);
            //                browserChoice.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            //                setupGrid.getChildren().addAll(test, browserChoice, test1, browserChoice1);
            //                calculateSizes();
            //                break;

            //set profile name
            3 -> {
                messageLabel!!.text = LanguageController.getString("desc_setup_3_0")
                nextButton!!.isDisable = true

                val testt = Label(LanguageController.getString("desc_setup_3_1"))
                testt.alignment = Pos.CENTER
                testt.maxHeight = 50.0
                GridPane.setConstraints(testt, 0, 0)

                val profileNameTextField = TextField()
                profileNameTextField.maxHeight = 50.0
                profileNameTextField.alignment = Pos.CENTER

                profileNameTextField.setOnKeyReleased { ke ->
                    if (!profileNameTextField.text.isEmpty()) {
                        nextButton!!.isDisable = false
                        PluginConfig.browserProfileName = profileNameTextField.text
                        if (ke.code == KeyCode.ENTER) {
                            nextButton!!.fire()
                        }
                    } else {
                        nextButton!!.isDisable = true
                    }
                }
                GridPane.setConstraints(profileNameTextField, 0, 1)


                //                ComboBox<BotType.Bot> typeField = new ComboBox<>();
                //                typeField.getItems().setAll(BotType.Bot.values());
                //                typeField.setMaxHeight(50);
                //                GridPane.setConstraints(profileNameTextField, 0, 2);

                setupGrid!!.children.addAll(testt, profileNameTextField)
                calculateSizes()
            }

            //set chrome config directory
            4 -> {
                messageLabel!!.text = LanguageController.getString("desc_setup_4_0")
                prevButton!!.isVisible = true
                nextButton!!.isDisable = true

                val testt2 = Label(LanguageController.getString("desc_setup_4_1"))
                testt2.alignment = Pos.CENTER
                testt2.maxHeight = 100.0
                GridPane.setConstraints(testt2, 0, 0)

                val searchChrome = Button(LanguageController.getString("search"))
                searchChrome.maxHeight = 100.0
                searchChrome.alignment = Pos.CENTER
                searchChrome.setOnAction { event ->
                    val directoryChooser = DirectoryChooser()
                    val selectedDirectory = directoryChooser.showDialog(MainScene.mainScene.windowScene!!.window)

                    if (selectedDirectory != null) {
                        setupGrid!!.children.clear()
                        val text = Label(selectedDirectory.path)
                        GridPane.setConstraints(text, 0, 0)
                        setupGrid!!.children.add(text)
                        PluginConfig.chromeConfigDirectory = selectedDirectory.path
                        nextButton!!.isDisable = false
                    }
                }
                GridPane.setConstraints(searchChrome, 0, 1)

                setupGrid!!.children.addAll(testt2, searchChrome)
                calculateSizes()
                nextButton!!.text = LanguageController.getString("finish")
            }

            5 -> {
                writeBrowserSetting() //save browser selection
                Configuration.config.saveProperties(PluginConfig::class.java)
                Configuration.config.saveProperties(BotConfig::class.java)
                MainScene.mainScene.borderPane.center = null
                val layout = MainLayout()
                MainScene.mainScene.borderPane.top = layout.launchNode
            }
        }
    }

    private fun getBrowserStyle(styleType: Int): String {
        when (styleType) {
            1 -> return "logo-button-chrome"
            2 -> return "logo-button-firefox"
            else -> return ""
        }
    }

    private fun calculateSizes() {
        for (child in setupGrid!!.children) {
            val tile = child as Control
            val margin = prefTileSize.get() / 15
            tile.setPrefSize(prefTileSize.get(), prefTileSize.get())
            GridPane.setMargin(child, Insets(margin))
        }
    }

    private fun writeBrowserSetting() {
        val deviceJson = arrayOf(JsonParser.readJsonFromFile(File(BotConfig.modulesDirFile.toString() + File.separator + "devices" + File.separator + PluginConfig.botType!!.getDisplayableType().toLowerCase() + ".json")))
        if (deviceJson[0] == null) {
            deviceJson[0] = JSONObject()
            deviceJson[0].put("browser", PluginConfig.botType)
        } else {
            deviceJson[0].put("browser", PluginConfig.botType)
        }
        JsonParser.writeJsonObjectToFile(deviceJson[0], File(BotConfig.modulesDirFile.toString() + File.separator + "devices"), PluginConfig.botType!!.getDisplayableType().toLowerCase() + ".json")
    }
}

