package net.rickiekarp.core.view

import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.LineChart.SortingPolicy
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import net.rickiekarp.core.provider.LocalizationProvider
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import kotlin.math.cos
import kotlin.math.sin

/**
 * About Stage GUI.
 */
class CoordinatesScene {
    private var viewTag = "location"

    private val content: BorderPane
        get() {

            val borderpane = BorderPane()
            borderpane.style = "-fx-background-color: #1d1d1d;"

            val xAxis = NumberAxis()
            val yAxis = NumberAxis()

            val series = Series<Number, Number>()
            series.name = "Data"

            for (i in 0..4) {
                val t = 2 * Math.PI * i / 4
                val x = cos(t)
                val y = sin(t)
                series.data.add(XYChart.Data(x, y))
            }

            val chart = LineChart(xAxis, yAxis)
            chart.data.add(series)
            val policies = FXCollections.observableArrayList(SortingPolicy.entries)

            val policy = ChoiceBox(policies)
            policy.tooltip = Tooltip("Choose a data sorting policy.")
            policy.selectionModel.selectFirst()
            chart.axisSortingPolicyProperty().bind(policy.valueProperty())

            val root: Pane = StackPane(chart, policy)
            StackPane.setAlignment(policy, Pos.TOP_RIGHT)

            borderpane.center = root
            return borderpane
        }

    init {
        create()
    }

    private fun create() {
        val infoStage = Stage()
        infoStage.title = LocalizationProvider.getString(viewTag)
        infoStage.icons.add(ImageLoader.getAppIconSmall())
        infoStage.isResizable = false
        infoStage.width = 1024.0
        infoStage.height = 720.0

        val contentVbox = BorderPane()
        contentVbox.center = content

        val aboutWindow = WindowScene(WindowStage(viewTag, infoStage), contentVbox, 1)

        infoStage.scene = aboutWindow
        infoStage.show()

        MainScene.stageStack.push(WindowStage(viewTag, infoStage))
    }
}