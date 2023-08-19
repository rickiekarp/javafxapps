package net.rickiekarp.sha1pass.view

import javafx.collections.FXCollections
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import net.rickiekarp.core.components.FoldableListCell
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.extensions.addCharAtIndex
import net.rickiekarp.core.extensions.removeCharAtIndex
import net.rickiekarp.core.model.SettingEntry
import net.rickiekarp.core.provider.LocalizationProvider
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.util.crypt.Md5Coder
import net.rickiekarp.core.util.math.MathUtil
import net.rickiekarp.core.util.random.AlphabetType
import net.rickiekarp.core.util.random.RandomCharacter
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.sha1pass.enum.TextCodingType


class TextCoding(textCodingType: TextCodingType) {
    private var grid: GridPane? = null
    private var controls: HBox? = null
    private var type = textCodingType
    private val WINDOWIDENTIFIER = "textcoding"

    private lateinit var seedTextField: TextField

    init {
        create()
    }

    private fun create() {
        val infoStage = Stage()
        infoStage.title = LocalizationProvider.getString(WINDOWIDENTIFIER)
        infoStage.icons.add(ImageLoader.getAppIconSmall())
        infoStage.isResizable = true
        infoStage.width = 900.0
        infoStage.height = 700.0

        val contentVbox = BorderPane()
        contentVbox.center = createLayout()

        val aboutWindow = WindowScene(WindowStage(WINDOWIDENTIFIER, infoStage), contentVbox, 1)

        infoStage.scene = aboutWindow
        infoStage.show()

        MainScene.stageStack.push(WindowStage(WINDOWIDENTIFIER, infoStage))
    }

    private val content: BorderPane
        get() {
            val borderpane = BorderPane()
            borderpane.style = "-fx-background-color: #1d1d1d;"

            val hBox = HBox()
            grid = GridPane()
            controls = HBox()

            grid!!.vgap = 8.0
            grid!!.hgap = 16.0
            grid!!.padding = Insets(20.0, 15.0, 20.0, 20.0)

            val title = Label(type.name)
            title.style = "-fx-font-size: 16pt;"
            GridPane.setHalignment(title, HPos.CENTER)
            GridPane.setConstraints(title, 0, 0)
            GridPane.setColumnSpan(title, 2)
            HBox.setHgrow(title, Priority.ALWAYS)
            grid!!.children.add(title)

            seedTextField = TextField()
            seedTextField.style = "-fx-font-size: 10pt;"
            seedTextField.tooltip = Tooltip(LocalizationProvider.getString("pass_peek_tip"))
            seedTextField.promptText = LocalizationProvider.getString("seed")
            GridPane.setConstraints(seedTextField, 0, 1)
            GridPane.setColumnSpan(seedTextField, 2)
            seedTextField.prefWidth = 30.0
            grid!!.children.add(seedTextField)

            val input = TextArea()
            GridPane.setConstraints(input, 0, 2)
            GridPane.setVgrow(input, Priority.ALWAYS)
            input.isWrapText = true
            grid!!.children.add(input)

            val output = TextArea()
            output.isEditable = false
            output.isWrapText = true
            GridPane.setConstraints(output, 1, 2)
            GridPane.setVgrow(output, Priority.ALWAYS)
            grid!!.children.add(output)

            controls!!.padding = Insets(10.0, 7.0, 10.0, 7.0)
            controls!!.spacing = 10.0
            controls!!.alignment = Pos.CENTER

            when (type) {
                TextCodingType.ENCODE -> {
                    val codingButton = Button(LocalizationProvider.getString("encode"))
                    codingButton.setOnAction { _ ->
                        output.clear()
                        var outputText = ""

                        val seed = RandomCharacter.getSeed(seedTextField.text)
                        val shuffledCharacters = RandomCharacter.getCharacterListShuffled(seed)

                        var inputText = input.text
                        inputText = inputText.trim().replace("[^a-zA-Z0-9]".toRegex(), "")

                        var index = 0
                        for (character in inputText) {
                            val seedCharacterAsInt = RandomCharacter.getCharacterFromSeed(index, seed)
                            val outChar = RandomCharacter.getCharacterAtIndex(RandomCharacter.letterToAlphabetPos(character) + seedCharacterAsInt, shuffledCharacters)
                            outputText += outChar.toString()
                            index++
                        }

                        val numberOfCharsToAdd = MathUtil.log2(seedTextField.text.length, 0)
                        val md5 = Md5Coder.calcMd5(seedTextField.text).replace("[^1-9]".toRegex(), "").substring(0, numberOfCharsToAdd)

                        for (md5Digit in md5.toSortedSet().sorted()) {
                            val randomCharacter = RandomCharacter.getRandomCharacter()
                            outputText = outputText.addCharAtIndex(randomCharacter, md5Digit.digitToInt())
                        }

                        output.text = outputText

                    }
                    controls!!.children.add(codingButton)
                }
                TextCodingType.DECODE -> {
                    val codingButton = Button(LocalizationProvider.getString("decode"))
                    codingButton.setOnAction { _ ->
                        output.clear()
                        var outputText = ""

                        val seed = RandomCharacter.getSeed(seedTextField.text)
                        val shuffledCharacters = RandomCharacter.getCharacterListShuffled(seed);

                        var inputText = input.text

                        val numberOfCharsToAdd = MathUtil.log2(seedTextField.text.length, 0)
                        val md5 = Md5Coder.calcMd5(seedTextField.text).replace("[^1-9]".toRegex(), "").substring(0, numberOfCharsToAdd)

                        for (md5Digit in md5.toSortedSet().sortedDescending()) {
                            inputText = inputText.removeCharAtIndex(md5Digit.digitToInt())
                        }

                        var index = 0
                        for (character in inputText) {
                            val seedCharacterAsInt = RandomCharacter.getCharacterFromSeed(index, seed)
                            val characterIndex = RandomCharacter.getIndexFromChar(character, shuffledCharacters) - seedCharacterAsInt
                            val decodedChar = RandomCharacter.alphabetPosToLetter(characterIndex)
                            outputText += decodedChar.toString()
                            index++
                        }

                        output.text = outputText
                    }
                    controls!!.children.add(codingButton)
                }
            }

            hBox.children.add(grid)
            hBox.alignment = Pos.CENTER

            borderpane.center = hBox
            borderpane.bottom = controls

            return borderpane
        }

    private fun createBox2(description: String): VBox {
        val content = VBox()
        content.spacing = 5.0

        val option2Desc = Label(LocalizationProvider.getString(description))
        option2Desc.isWrapText = true
        option2Desc.style = "-fx-font-size: 9pt;"
        option2Desc.maxWidth = 175.0

        val latin = CheckBox(LocalizationProvider.getString("latin"))
        latin.isSelected = true
        RandomCharacter.characterSetConfig[AlphabetType.LATIN] = true
        latin.setOnAction { _ ->
            RandomCharacter.characterSetConfig[AlphabetType.LATIN] = !RandomCharacter.characterSetConfig[AlphabetType.LATIN]!!
            LogFileHandler.logger.config("change_filename_option: " + !RandomCharacter.characterSetConfig[AlphabetType.LATIN]!! + " -> " + RandomCharacter.characterSetConfig[AlphabetType.LATIN])
        }

        val cyrillic = CheckBox(LocalizationProvider.getString("cyrillic"))
        cyrillic.isSelected = true
        RandomCharacter.characterSetConfig[AlphabetType.CYRILLIC] = true
        cyrillic.setOnAction { _ ->
            RandomCharacter.characterSetConfig[AlphabetType.CYRILLIC] = !RandomCharacter.characterSetConfig[AlphabetType.CYRILLIC]!!
            LogFileHandler.logger.config("change_filename_option: " + !RandomCharacter.characterSetConfig[AlphabetType.CYRILLIC]!! + " -> " + RandomCharacter.characterSetConfig[AlphabetType.CYRILLIC])
        }

        val greek = CheckBox(LocalizationProvider.getString("greek"))
        greek.isSelected = true
        RandomCharacter.characterSetConfig[AlphabetType.GREEK] = true
        greek.setOnAction { _ ->
            RandomCharacter.characterSetConfig[AlphabetType.GREEK] = !RandomCharacter.characterSetConfig[AlphabetType.GREEK]!!
            LogFileHandler.logger.config("change_filename_option: " + !RandomCharacter.characterSetConfig[AlphabetType.GREEK]!! + " -> " + RandomCharacter.characterSetConfig[AlphabetType.GREEK])
        }

        content.children.addAll(option2Desc, latin, cyrillic, greek)
        return content
    }

    private fun createBox1(description: String): VBox {
        val content = VBox()
        content.spacing = 5.0

        val option1Desc = Label(LocalizationProvider.getString(description))
        option1Desc.isWrapText = true
        option1Desc.style = "-fx-font-size: 9pt;"
        option1Desc.maxWidth = 175.0

        val cBoxVersion = ComboBox<String>()
        cBoxVersion.items.addAll("v1", "v2")
        cBoxVersion.selectionModel.select(0)
        cBoxVersion.minWidth = 100.0

        val hBox = HBox()
        hBox.alignment = Pos.CENTER_LEFT
        hBox.spacing = 5.0
        hBox.children.addAll(cBoxVersion)

        content.children.addAll(option1Desc, hBox)
        return content
    }

    private fun createLayout(): Node {
        val mainContent = BorderPane()

        val columnConstraints = ColumnConstraints()
        columnConstraints.isFillWidth = true
        columnConstraints.hgrow = Priority.ALWAYS

        val controls = AnchorPane()
        controls.minHeight = 50.0

        val settingsGrid = GridPane()
        settingsGrid.prefWidth = 200.0
        settingsGrid.vgap = 10.0
        settingsGrid.padding = Insets(5.0, 0.0, 0.0, 0.0)  //padding top, left, bottom, right
        settingsGrid.alignment = Pos.BASELINE_CENTER

        //SETTINGS LIST
        val list = ListView<SettingEntry>()
        GridPane.setConstraints(list, 0, 0)
        GridPane.setVgrow(list, Priority.ALWAYS)
        settingsGrid.children.add(list)

        val items = FXCollections.observableArrayList<SettingEntry>()
        items.add(SettingEntry("Setting.AlgorithmVersion.Title",false, createBox1("Setting.AlgorithmVersion.Description")))
        items.add(SettingEntry("Setting.CharacterSet.Title",false, createBox2("Setting.CharacterSet.Description")))
        list.items = items

        list.setCellFactory { FoldableListCell(list) }

        GridPane.setConstraints(settingsGrid, 1, 0)

        //add components to borderpane
        mainContent.center = content
        mainContent.right = settingsGrid

        //debug colors
        if (DebugHelper.isDebugVersion) {
            controls.style = "-fx-background-color: #336699;"
            settingsGrid.style = "-fx-background-color: #444444"
            settingsGrid.isGridLinesVisible = true
        } else {
            controls.style = "-fx-background-color: #1d1d1d;"
            settingsGrid.style = null
            settingsGrid.isGridLinesVisible = false
        }

        return mainContent
    }
}