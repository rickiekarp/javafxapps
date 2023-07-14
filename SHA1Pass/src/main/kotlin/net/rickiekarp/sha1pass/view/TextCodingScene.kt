package net.rickiekarp.sha1pass.view

import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import net.rickiekarp.core.AppContext
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.provider.LocalizationProvider
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.util.random.RandomCharacter
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.sha1pass.*
import net.rickiekarp.sha1pass.enum.TextCodingType


class TextCodingScene(textCodingType: TextCodingType) {
    private var grid: GridPane? = null
    private var controls: HBox? = null
    private var type = textCodingType
    private val WINDOWIDENTIFIER = "textcoding"

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
            grid!!.minWidth = 180.0

            val title = Label(AppContext.context.applicationName)
            title.style = "-fx-font-size: 16pt;"
            GridPane.setHalignment(title, HPos.CENTER)
            GridPane.setConstraints(title, 0, 0)
            GridPane.setColumnSpan(title, 2)
            grid!!.children.add(title)

            val input = TextArea()
            GridPane.setConstraints(input, 0, 1)
            input.isWrapText = true
            grid!!.children.add(input)

            val output = TextArea()
            output.isEditable = false
            output.isWrapText = true
            GridPane.setConstraints(output, 1, 1)
            grid!!.children.add(output)

            GridPane.setHgrow(title, Priority.ALWAYS)
            GridPane.setVgrow(input, Priority.ALWAYS)

            controls!!.padding = Insets(10.0, 7.0, 10.0, 7.0)
            controls!!.spacing = 10.0
            controls!!.alignment = Pos.CENTER

            when (type) {
                TextCodingType.ENCODE -> {
                    val codingButton = Button(LocalizationProvider.getString("encode"))
                    codingButton.setOnAction { _ ->
                        var text = input.text
                        text = text.trim()
                        text = text.replace("[^a-zA-Z0-9]".toRegex(), "");
                        output.clear()
                        for (i in text) {
                            val outChar = RandomCharacter.getCharacterAtIndex(RandomCharacter.letterToAlphabetPos(i))
                            output.appendText(outChar.toString())
                        }
                    }
                    controls!!.children.add(codingButton)
                }
                TextCodingType.DECODE -> {
                    val codingButton = Button(LocalizationProvider.getString("decode"))
                    codingButton.setOnAction { _ ->
                        val text = input.text
                        output.clear()
                        for (i in text) {
                            val outChar = RandomCharacter.getIndexFromChar(i)
                            val char = RandomCharacter.alphabetPosToLetter(outChar)
                            println(char)
                            output.appendText(char.toString())
                        }
                    }
                    controls!!.children.add(codingButton)
                }
            }

            hBox.children.add(grid)
            HBox.setHgrow(grid, Priority.ALWAYS)

            borderpane.center = hBox
            borderpane.bottom = controls

            return borderpane
        }

    init {
        create()
    }

    private fun create() {
        val infoStage = Stage()
        infoStage.title = LocalizationProvider.getString(WINDOWIDENTIFIER)
        infoStage.icons.add(ImageLoader.getAppIconSmall())
        infoStage.isResizable = false
        infoStage.width = 800.0
        infoStage.height = 600.0

        val contentVbox = BorderPane()
        contentVbox.center = content

        val aboutWindow = WindowScene(WindowStage(WINDOWIDENTIFIER, infoStage), contentVbox, 1)

        infoStage.scene = aboutWindow
        infoStage.show()

        debugAbout()

        MainScene.stageStack.push(WindowStage(WINDOWIDENTIFIER, infoStage))
    }

    private fun debugAbout() {
        if (DebugHelper.isDebugVersion) {
            grid!!.isGridLinesVisible = true
            grid!!.style = "-fx-background-color: #333333;"
            controls!!.style = "-fx-background-color: #336699;"
        } else {
            grid!!.isGridLinesVisible = false
            grid!!.style = null
            controls!!.style = null
        }
    }
}