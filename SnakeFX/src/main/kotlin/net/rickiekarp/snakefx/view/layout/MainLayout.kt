package net.rickiekarp.snakefx.view.layout

import javafx.animation.Animation
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*
import javafx.stage.Modality
import javafx.stage.Stage
import net.rickiekarp.core.AppContext
import net.rickiekarp.core.components.FoldableListCell
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.model.SettingEntry
import net.rickiekarp.core.net.NetResponse
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.view.layout.AppLayout
import net.rickiekarp.snakefx.core.NewGameFunction
import net.rickiekarp.snakefx.core.SpeedLevel
import net.rickiekarp.snakefx.highscore.HighScoreEntry
import net.rickiekarp.snakefx.highscore.HighscoreJsonDao
import net.rickiekarp.snakefx.highscore.HighscoreManager
import net.rickiekarp.snakefx.net.SnakeNetworkApi
import net.rickiekarp.snakefx.settings.Config
import net.rickiekarp.snakefx.view.ViewModel
import net.rickiekarp.snakefx.view.presenter.MainPresenter

class MainLayout(val mainPresenter: MainPresenter, private val viewModel: ViewModel, private val highscoreManager: HighscoreManager) : AppLayout {
    lateinit var playPause: Button
    lateinit var points: Label
    lateinit var speed: ChoiceBox<SpeedLevel>
    lateinit var table: TableView<HighScoreEntry>

    val LABEL_START = "Start"
    val LABEL_RESUME = "Resume"
    val LABEL_PAUSE = "Pause"

    val gridContainer: Pane = mainPresenter.gridContainer

    init {
        viewModel.collision.addListener { observable, oldValue, collisionHappend ->
            if (collisionHappend!!) {
                gameFinished()
            }
        }
    }

    override val layout: Node
        get() {
            val borderpane = BorderPane()
            borderpane.style = "-fx-background-color: #1d1d1d;"

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
            items.add(SettingEntry("setting_1",false, getOptions("setting_1_desc")))
            list.items.setAll(items)

            list.setCellFactory { FoldableListCell(list) }

            borderpane.center = gridContainer
            borderpane.right = content
            borderpane.top = getPanel()

            return borderpane
        }

    private fun getPanel(): Node {
        val hbox = HBox()
        hbox.spacing = 15.0
        hbox.prefWidth = 74.0
        hbox.alignment = Pos.CENTER_LEFT
        hbox.padding = Insets(10.0, 10.0, 10.0, 10.0)

        val pointsText = Label("Points: ")
        points = Label("0")
        playPause = Button("Start")
        playPause.setOnAction { togglePlayPause() }

        val newGame = Button("New Game")
        newGame.setOnAction { mainPresenter.newGame() }

        val difficultyLabel = Label("Difficulty:")

        speed = ChoiceBox()

        hbox.children.addAll(pointsText, points, playPause, newGame, difficultyLabel, speed)

        initialize()

        return hbox
    }

    fun initialize() {
        speed.itemsProperty().get().addAll(*SpeedLevel.values())
        points.textProperty().bind(viewModel.points.asString())
        speed.getSelectionModel().selectFirst()
        speed.valueProperty().bindBidirectional(viewModel.speed)
        playPause.disableProperty().bind(viewModel.collision)
        viewModel.gameloopStatus.addListener { observable: ObservableValue<out Animation.Status>?, oldStatus: Animation.Status?, newStatus: Animation.Status ->
            if (Animation.Status.STOPPED == newStatus) {
                playPause.textProperty().set(LABEL_START)
            }
        }
    }

    fun togglePlayPause() {
        val status = viewModel.gameloopStatus.get()
        when (status) {
            Animation.Status.PAUSED -> {
                playPause.textProperty().set(LABEL_PAUSE)
                viewModel.gameloopStatus.set(Animation.Status.RUNNING)
            }
            Animation.Status.RUNNING -> {
                playPause.textProperty().set(LABEL_RESUME)
                viewModel.gameloopStatus.set(Animation.Status.PAUSED)
            }
            Animation.Status.STOPPED -> {
                playPause.textProperty().set(LABEL_PAUSE)
                viewModel.gameloopStatus.set(Animation.Status.RUNNING)
            }
        }
    }

    private
    val content: BorderPane
        get() {

            val borderpane = BorderPane()
            borderpane.style = "-fx-background-color: #1d1d1d;"

            val vbox = VBox()
            vbox.padding = Insets(10.0, 10.0,10.0,10.0)

            val label = Label("Highscore")

            table = TableView()
            table.isFocusTraversable = false
            table.prefHeight = 450.0
            table.prefWidth = 300.0
            table.placeholder = Label("No Highscore yet")


            val id = TableColumn<HighScoreEntry, String>(LanguageController.getString("rank"))
            id.cellValueFactory = PropertyValueFactory("ranking")
            id.minWidth = 60.0

            val name = TableColumn<HighScoreEntry, String>(LanguageController.getString("name"))
            name.cellValueFactory = PropertyValueFactory("name")

            val points = TableColumn<HighScoreEntry, String>(LanguageController.getString("points"))
            points.cellValueFactory = PropertyValueFactory("points")
            points.minWidth = 80.0

            val date = TableColumn<HighScoreEntry, String>(LanguageController.getString("date"))
            date.cellValueFactory = PropertyValueFactory("dateAdded")

            table.columns.addAll(id, name, points, date)


            vbox.children.add(label)
            vbox.children.add(table)

            borderpane.center = vbox

            return borderpane
        }

    private fun getOptions(description: String): VBox {
        val content = VBox()
        content.spacing = 5.0

        content.children.add(Label(description))

        return content
    }

    override fun postInit() {
        val response = AppContext.context.networkApi.requestResponse(SnakeNetworkApi.requestRanking())
        if (response!!.code != 200) {
            table.placeholder = Label("Could not load highscores!")
        } else {
            val responseBody = NetResponse.getResponseString(response)
            val items = HighscoreJsonDao().load(responseBody!!)
            for (i in 0 until items.size) {
                items[i].ranking = i + 1
                highscoreManager.highScoreEntries().add(items[i])
            }
            table.items.setAll(highscoreManager.highScoreEntries())
        }
    }

    fun gameFinished() {
        val points = viewModel.points.get()
        val size = highscoreManager.highScoreEntries().size

        if (size < Config.MAX_SCORE_COUNT.get()) {

            Platform.runLater() {
                if (confirmDialog("submit_desc", 400, 250)) {
                    table.items.clear()
                    table.items.setAll(highscoreManager.highScoreEntries())
                }
            }

        } else {
            // check whether the last entry on the list has more points then the
            // current game

            if (highscoreManager.highScoreEntries().get(size - 1).points < points) {
                viewModel.newHighscoreWindowOpen.set(true)
            }
        }
    }


    fun confirmDialog(msg: String, width: Int, height: Int): Boolean {
        val modalDialog = Stage()
        modalDialog.icons.add(ImageLoader.getAppIconSmall())
        modalDialog.initModality(Modality.APPLICATION_MODAL)
        modalDialog.isResizable = false
        modalDialog.width = width.toDouble()
        modalDialog.height = height.toDouble()
        modalDialog.title = LanguageController.getString("highscore")

        val bool = BooleanArray(1)

        val contentVbox = VBox()
        contentVbox.spacing = 5.0
        contentVbox.padding = Insets(0.0, 30.0, 0.0, 30.0)

        val optionHBox = HBox()
        optionHBox.spacing = 20.0
        optionHBox.alignment = Pos.CENTER
        optionHBox.padding = Insets(5.0, 0.0, 15.0, 0.0)

        val label = Label(LanguageController.getString(msg))
        label.padding = Insets(20.0, 0.0, 10.0, 20.0)
        label.isWrapText = true

        val points = Label("Points: " + viewModel.points.get())
        points.padding = Insets(0.0, 0.0, 15.0, 20.0)

        val nameLabel = Label(LanguageController.getString("name"))
        val textfield = TextField()
        textfield.promptText = "Name"

        val yesButton = Button(LanguageController.getString("ok"))
        yesButton.setOnAction { event ->
            if (!highscoreManager.isNameValid(textfield.text)) {
                nameLabel.text = "Name is invalid!"
                return@setOnAction
            }

            highscoreManager.addScore(textfield.text, viewModel.points.get())
            bool[0] = true
            modalDialog.close()
        }

        optionHBox.children.addAll(yesButton)
        contentVbox.children.addAll(label, points, nameLabel, textfield, optionHBox)
        VBox.setVgrow(contentVbox, Priority.ALWAYS)

        // The Window as a Scene
        val modalDialogScene = WindowScene(WindowStage("confirm", modalDialog), contentVbox, 1)

        modalDialog.scene = modalDialogScene

        LogFileHandler.logger.info("open.highscoreDialog")

        modalDialog.showAndWait()

        return bool[0]
    }
}
