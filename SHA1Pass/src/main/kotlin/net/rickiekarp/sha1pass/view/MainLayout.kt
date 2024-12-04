package net.rickiekarp.sha1pass.view

import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import net.rickiekarp.core.AppContext
import net.rickiekarp.core.components.textfield.CustomTextField
import net.rickiekarp.core.components.textfield.CustomTextFieldSkin
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.enums.AlphabetType
import net.rickiekarp.core.extensions.addCharAtIndex
import net.rickiekarp.core.provider.LocalizationProvider
import net.rickiekarp.core.util.ClipboardUtil
import net.rickiekarp.core.util.CommonUtil
import net.rickiekarp.core.util.crypt.*
import net.rickiekarp.core.util.random.RandomCharacter
import net.rickiekarp.core.view.AboutScene
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.core.view.layout.AppLayout
import net.rickiekarp.sha1pass.enum.TextCodingType
import net.rickiekarp.sha1pass.libloader.GoLibTransformer
import net.rickiekarp.sha1pass.scenes.CoordinatesScene
import net.rickiekarp.sha1pass.settings.AppConfiguration

class MainLayout : AppLayout {
    private var isSecure = false
    private var hmac = false
    private var complex = true
    private var isSpecialCharacterMode = true

    private lateinit var mainGrid: GridPane
    private lateinit var color: Rectangle
    private lateinit var sentenceTF: CustomTextField
    private lateinit var sentenceTextFieldSkin: CustomTextFieldSkin
    private lateinit var wordTF: CustomTextField
    private lateinit var wordTextFieldSkin: CustomTextFieldSkin

    private var colorPos = -1

    private val mainLayout: Node
        get() {
            val mainContent = BorderPane()

            mainGrid = GridPane()
            mainGrid.padding = Insets(5.0, 3.0, 0.0, 3.0)

            val encryptBtns = HBox()
            encryptBtns.padding = Insets(0.0, 0.0, 0.0, 0.0)
            encryptBtns.spacing = 7.0
            encryptBtns.alignment = Pos.CENTER_LEFT

            val controls = HBox()
            controls.padding = Insets(3.0, 3.0, 3.0, 7.0)
            controls.spacing = 2.0

            val status = Label(AppContext.context.applicationName)
            status.style = "-fx-font-size: 9pt;"
            controls.children.add(status)
            val column1 = ColumnConstraints()
            column1.percentWidth = 12.0
            val column2 = ColumnConstraints()
            column2.percentWidth = 15.0
            val column3 = ColumnConstraints()
            column3.percentWidth = 19.0
            val column4 = ColumnConstraints()
            column4.percentWidth = 17.0
            val column5 = ColumnConstraints()
            column5.percentWidth = 16.0
            val column6 = ColumnConstraints()
            column6.percentWidth = 16.0
            val column7 = ColumnConstraints()
            column7.percentWidth = 11.0
            mainGrid.columnConstraints.addAll(column1, column2, column3, column4, column5, column6, column7)

            mainGrid.hgap = 5.0
            mainGrid.vgap = 5.0
            val sentenceLabel = Label(LocalizationProvider.getString("u_sentence"))
            sentenceLabel.style = "-fx-font-size: 9pt;"
            GridPane.setConstraints(sentenceLabel, 0, 0)
            GridPane.setHalignment(sentenceLabel, HPos.CENTER)
            mainGrid.children.add(sentenceLabel)
            sentenceLabel.tooltip = Tooltip(LocalizationProvider.getString("type_sentence_tip"))

            sentenceTF = CustomTextField()
            sentenceTF.tooltip = Tooltip(LocalizationProvider.getString("type_sentence_tip"))
            sentenceTextFieldSkin = CustomTextFieldSkin(sentenceTF)
            sentenceTextFieldSkin.shouldMask = true
            sentenceTF.skin = sentenceTextFieldSkin
            GridPane.setConstraints(sentenceTF, 1, 0)
            GridPane.setColumnSpan(sentenceTF, 4)
            mainGrid.children.add(sentenceTF)

            val coordinatesBtn = Button("L")
            coordinatesBtn.style = "-fx-font-size: 9pt;"
            coordinatesBtn.tooltip = Tooltip(
                LocalizationProvider.getString("help_tip") + " " + AppContext.context.applicationName
            )
            coordinatesBtn.setOnAction { CoordinatesScene() }
            GridPane.setConstraints(coordinatesBtn, 6, 0)
            GridPane.setHalignment(coordinatesBtn, HPos.CENTER)
            mainGrid.children.add(coordinatesBtn)

            val viewMode = CheckBox(LocalizationProvider.getString("vs"))
            viewMode.style = "-fx-font-size: 9pt;"
            viewMode.tooltip = Tooltip(LocalizationProvider.getString("vs_tip"))
            GridPane.setConstraints(viewMode, 5, 0)
            GridPane.setHalignment(viewMode, HPos.CENTER)
            mainGrid.children.add(viewMode)

            val secureMode = CheckBox(LocalizationProvider.getString("sm"))
            secureMode.style = "-fx-font-size: 8pt;"
            secureMode.tooltip = Tooltip(LocalizationProvider.getString("sm_tip"))
            GridPane.setConstraints(secureMode, 4, 1)
            mainGrid.children.add(secureMode)

            val hmacMode = CheckBox(LocalizationProvider.getString("hm"))
            hmacMode.style = "-fx-font-size: 8pt;"
            hmacMode.tooltip = Tooltip(LocalizationProvider.getString("hmac_tip"))
            hmacMode.isSelected = hmac
            GridPane.setConstraints(hmacMode, 1, 1)
            mainGrid.children.add(hmacMode)

            val complexMode = CheckBox(LocalizationProvider.getString("comp"))
            complexMode.style = "-fx-font-size: 8pt;"
            complexMode.tooltip = Tooltip(LocalizationProvider.getString("comp_tip"))
            complexMode.isSelected = complex
            GridPane.setConstraints(complexMode, 2, 1)
            mainGrid.children.add(complexMode)

            val specialCheckBox = CheckBox(LocalizationProvider.getString("specialMode"))
            specialCheckBox.style = "-fx-font-size: 8pt;"
            specialCheckBox.tooltip = Tooltip(LocalizationProvider.getString("specialMode_tip"))
            specialCheckBox.isSelected = isSpecialCharacterMode
            GridPane.setConstraints(specialCheckBox, 3, 1)
            mainGrid.children.add(specialCheckBox)

            wordTF = CustomTextField()
            wordTextFieldSkin = CustomTextFieldSkin(wordTF)
            wordTextFieldSkin.shouldMask = false
            wordTF.skin = wordTextFieldSkin
            wordTF.text = CommonUtil.getDate("yyyy")
            wordTF.style = "-fx-font-size: 10pt;"
            wordTF.tooltip = Tooltip(LocalizationProvider.getString("pass_word_tip"))
            GridPane.setConstraints(wordTF, 5, 1)
            wordTF.prefWidth = 30.0
            mainGrid.children.add(wordTF)

            val colorBtn = Button(LocalizationProvider.getString("color_label"))
            colorBtn.style = "-fx-font-size: 9pt;"
            colorBtn.tooltip = Tooltip(LocalizationProvider.getString("color_tip"))
            GridPane.setConstraints(colorBtn, 6, 1)
            GridPane.setHalignment(colorBtn, HPos.CENTER)
            mainGrid.children.add(colorBtn)

            val hexBtn = Button(LocalizationProvider.getString("hex_label"))
            hexBtn.style = "-fx-font-size: 10pt;"
            hexBtn.tooltip = Tooltip(LocalizationProvider.getString("a_40_char_tip"))
            hexBtn.minWidth = 103.0
            encryptBtns.children.add(hexBtn)

            val b64Btn = Button(LocalizationProvider.getString("b64_label"))
            b64Btn.style = "-fx-font-size: 10pt;"
            b64Btn.tooltip = Tooltip(LocalizationProvider.getString("a_28_char_tip"))
            b64Btn.minWidth = 103.0
            encryptBtns.children.add(b64Btn)

            val bcryptBtn = Button(LocalizationProvider.getString("bcrypt_label"))
            bcryptBtn.style = "-fx-font-size: 10pt;"
            bcryptBtn.tooltip = Tooltip(LocalizationProvider.getString("a_60_char_tip"))
            bcryptBtn.minWidth = 103.0
            encryptBtns.children.add(bcryptBtn)

            color = Rectangle(30.0, 30.0)
            color.isVisible = false
            color.fill = Color.TRANSPARENT
            GridPane.setHalignment(color, HPos.CENTER)
            GridPane.setConstraints(color, 6, 2)
            mainGrid.children.add(color)

            mainGrid.children.add(encryptBtns)
            GridPane.setConstraints(encryptBtns, 1, 2)
            GridPane.setColumnSpan(encryptBtns, 5)


            mainContent.center = mainGrid
            mainContent.bottom = controls

            colorBtn.setOnAction {
                color.isVisible = true
                colorRotate()
            }

            hexBtn.setOnAction {
                calcHex()
                status.text = LocalizationProvider.getString("hex_password_copied")
            }

            b64Btn.setOnAction {
                calcBase64()
                status.text = LocalizationProvider.getString("b64_password_copied")
            }

            bcryptBtn.setOnAction {
                calcBCrypt()
                status.text = LocalizationProvider.getString("bcrypt_password_copied")
            }

            viewMode.selectedProperty().addListener { _, _, newVal ->
                if (newVal!!) {
                    sentenceTextFieldSkin.shouldMask = false
                    status.text = LocalizationProvider.getString("vs_on")
                } else {
                    sentenceTextFieldSkin.shouldMask = true
                    status.text = LocalizationProvider.getString("vs_off")
                }

                sentenceTF.text = sentenceTF.text
            }

            secureMode.selectedProperty().addListener { _, _, newVal ->
                if (newVal!!) {
                    isSecure = true
                    viewMode.isDisable = true
                    viewMode.isSelected = false
                    colorBtn.isDisable = true
                    sentenceTextFieldSkin.shouldMask = true
                    sentenceTF.text = sentenceTF.text
                    wordTextFieldSkin.shouldMask = true
                    wordTF.text = wordTF.text
                    status.text = LocalizationProvider.getString("sm_on")
                } else {
                    isSecure = false
                    viewMode.isDisable = false
                    sentenceTF.text = ""
                    wordTextFieldSkin.shouldMask = false
                    wordTF.text = CommonUtil.getDate("yyyy")
                    colorBtn.isDisable = false
                    ClipboardUtil.setStringToClipboard("")
                    status.text = LocalizationProvider.getString("sm_off")
                }
            }

            hmacMode.selectedProperty().addListener { _, _, newVal ->
                if (newVal) {
                    hmac = true
                    status.text = LocalizationProvider.getString("hmac_on")
                } else {
                    hmac = false
                    status.text = LocalizationProvider.getString("hmac_off")
                }
            }

            complexMode.selectedProperty().addListener { _, _, newVal ->
                if (newVal) {
                    complex = true
                    status.text = LocalizationProvider.getString("comp_on")
                } else {
                    complex = false
                    status.text = LocalizationProvider.getString("comp_off")
                }
            }

            specialCheckBox.selectedProperty().addListener { _, _, newVal ->
                isSpecialCharacterMode = newVal
                if (newVal) {
                    status.text = LocalizationProvider.getString("specialMode_on")
                } else {
                    status.text = LocalizationProvider.getString("specialMode_off")
                }
            }

            if (DebugHelper.DEBUG) {
                mainGrid.isGridLinesVisible = true
                mainGrid.style = "-fx-background-color: gray"
                encryptBtns.style = "-fx-background-color: #A36699;"
                controls.style = "-fx-background-color: #336699;"
            }

            setupEncryptDecryptButtons()

            val helpBtn = Button(LocalizationProvider.getString("help_label"))
            helpBtn.styleClass.add("decoration-button-other")
            helpBtn.tooltip = Tooltip(
                LocalizationProvider.getString("help_tip") + " " + AppContext.context.applicationName
            )
            helpBtn.setOnAction { AboutScene() }

            MainScene.mainScene.windowScene!!.win.titleBarButtonBox.children.addAll(helpBtn)
            return mainContent
        }

    private fun setupEncryptDecryptButtons() {
        val decodeButton = Button(LocalizationProvider.getString("decode_label_short"))
        decodeButton.style = "-fx-font-size: 9pt;"
        decodeButton.tooltip = Tooltip(LocalizationProvider.getString("decode_tip"))
        GridPane.setConstraints(decodeButton, 0, 1)
        GridPane.setHalignment(decodeButton, HPos.CENTER)
        mainGrid.children.add(decodeButton)
        decodeButton.setOnAction { TextCoding(TextCodingType.DECODE) }

        val encodeButton = Button(LocalizationProvider.getString("encode_label_short"))
        encodeButton.style = "-fx-font-size: 9pt;"
        encodeButton.tooltip = Tooltip(LocalizationProvider.getString("encode_tip"))
        GridPane.setConstraints(encodeButton, 0, 2)
        GridPane.setHalignment(encodeButton, HPos.CENTER)
        mainGrid.children.add(encodeButton)
        encodeButton.setOnAction { TextCoding(TextCodingType.ENCODE) }
    }

    private fun calcHex() {
        var result = GoLibTransformer.Sha1PassLib.EncodeToHex(checkInputData())

        if (complex) {
            result += AppConfiguration.comp_string
        }
        copyToClipboard(result)
    }

    private fun calcBase64() {
        val sha1 = SHA1Coder.getSHA1Bytes(checkInputData(), hmac)
        var hash = String(Base64Coder.encode(sha1))

        if (complex) {
            hash += AppConfiguration.comp_string
        }
        copyToClipboard(hash)
    }

    private fun calcBCrypt() {
        val data = checkInputData()
        val sha1 = SHA1Coder.getSHA1Bytes(data, hmac)
        val salt = "$2a$10$" + SHA1Coder.getSHA1String(sha1)
        var hash = BCryptCoder.hashPw(data, salt)

        if (complex) {
            hash += AppConfiguration.comp_string
        }
        copyToClipboard(hash)
    }

    private fun copyToClipboard(data: String) {
        ClipboardUtil.setStringToClipboard(data)
    }

    /**
     * if a color is selected, the hex color code is added to the entered data string
     * @return to be encrypted user input
     */
    private fun checkInputData(): String {
        var finalInput = sentenceTF.text + wordTF.text

        if (isSpecialCharacterMode) {
            if (finalInput.isNotEmpty()) {
                var divisor = 2
                if (finalInput.length > 5)
                {
                    divisor = if (isEven(finalInput.length)) {
                        3
                    } else {
                        4
                    }
                }

                for (i in 1..finalInput.length / divisor) {
                    val indexToFetch = i * divisor - 1
                    val randomChar = RandomCharacter.getCharacterAtIndex(indexToFetch, mutableMapOf(
                        AlphabetType.CYRILLIC to true, AlphabetType.LATIN to true, AlphabetType.GREEK to true,
                    ))
                    finalInput = finalInput.addCharAtIndex(randomChar, indexToFetch)
                }
            }
        }

        if (color.fill != Color.TRANSPARENT) {
            finalInput += ColorCoder.colorArray[colorPos].toString()
        }

        return finalInput
    }

    private fun isEven(num : Int) : Boolean {
       return num % 2 == 0
    }

    /**
     * Changes the color field
     */
    private fun colorRotate() {
        colorPos++

        if (colorPos == ColorCoder.colorArray.size) {
            color.fill = Color.TRANSPARENT
            colorPos = -1
        } else {
            color.fill = ColorCoder.colorArray[colorPos]
        }
    }

    override val layout: Node
        get() = mainLayout

    override fun postInit() {
        sentenceTF.requestFocus()
    }
}
