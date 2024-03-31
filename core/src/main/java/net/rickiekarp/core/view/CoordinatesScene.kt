package net.rickiekarp.core.view

import javafx.beans.binding.ObjectExpression
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.chart.LineChart
import javafx.scene.chart.LineChart.SortingPolicy
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.shape.Circle
import javafx.stage.Stage
import net.rickiekarp.core.provider.LocalizationProvider
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage


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

            val data1 = XYChart.Data<Number,Number>(-2, -3)
            data1.node = createDataNode(data1.XValueProperty(), data1.YValueProperty())
            data1.node.style = "-fx-background-color: red, red;"

            val data2 = XYChart.Data<Number,Number>(2, 3)
            data2.node = createDataNode(data2.XValueProperty(), data2.YValueProperty())
            data2.node.style = "-fx-background-color: orange, orange;"

            val data3 = XYChart.Data<Number,Number>(2, 6)
            data3.node = createDataNode(data3.XValueProperty(), data3.YValueProperty())
            data3.node.style = "-fx-background-color: red, red;"

            val data4 = XYChart.Data<Number,Number>(-1, 2)
            data4.node = createDataNode(data4.XValueProperty(), data4.YValueProperty())
            data4.node.style = "-fx-background-color: orange, orange;"

            series.data.addAll(data1, data2, data3, data4)

            val formula = calcLinearFunctionFormula(
                Point2D(data1.XValueProperty().value.toDouble(), data1.YValueProperty().value.toDouble()),
                Point2D(data3.XValueProperty().value.toDouble(), data3.YValueProperty().value.toDouble())
            )
            val formulaB = calcLinearFunctionFormula(
                Point2D(data2.XValueProperty().value.toDouble(), data2.YValueProperty().value.toDouble()),
                Point2D(data4.XValueProperty().value.toDouble(), data4.YValueProperty().value.toDouble())
            )
            val intersection = calcIntersectionPoint(formula, formulaB)

            if (intersection != null) {
                val intersectionNode = XYChart.Data<Number,Number>(intersection.first, intersection.second)
                intersectionNode.node = createDataNode(intersectionNode.XValueProperty(), intersectionNode.YValueProperty())
                intersectionNode.node.style = "-fx-background-color: green, green;"
                series.data.add(intersectionNode)
            }

            val chart = LineChart(xAxis, yAxis)
            chart.isLegendVisible = false
            chart.data.add(series)
            val policies = FXCollections.observableArrayList(SortingPolicy.entries)

            val policy = ChoiceBox(policies)
            policy.tooltip = Tooltip("Choose a data sorting policy.")
            policy.selectionModel.selectFirst()
            chart.axisSortingPolicyProperty().bind(policy.valueProperty())

            val nodes: Set<Node> = chart.lookupAll(".series0")
            nodes.first().style = "-fx-stroke: transparent;"

            val root: Pane = StackPane(chart, policy)
            StackPane.setAlignment(policy, Pos.TOP_RIGHT)

            borderpane.center = root
            return borderpane
        }

    private fun calcLinearFunctionFormula(pointA: Point2D, pointB: Point2D) : Pair<Double, Double> {
        val t = pointB.y - pointA.y
        val x = pointB.x - pointA.x

        val m = t / x
        val b = pointA.y - m * pointA.x
        return Pair(m, b)
    }

    private fun calcIntersectionPoint(formula: Pair<Double, Double>, formulaB: Pair<Double, Double>) : Pair<Double, Double>? {
        val m1: Double
        val b1: Double
        if (formula.first > formulaB.first) {
            m1 = formula.first - formulaB.first
            b1 = formulaB.second - formula.second
        } else {
            m1 = formulaB.first - formula.first
            b1 = formula.second - formulaB.second
        }

        val x = b1 / m1
        val y = formula.first * x + formula.second
        val yVerify = formulaB.first * x + formulaB.second

        return if (y == yVerify) {
            Pair(x, y)
        } else {
            null
        }
    }

    private fun createDataNode(xValue: ObjectExpression<Number>, yValue: ObjectExpression<Number>): Node {
        val label = Label()
        label.text = "(" + xValue.value.toString() + "," + yValue.value.toString() + ")"

        val pane = Pane(label)
        pane.shape = Circle(6.0)
        pane.isScaleShape = false

        label.translateYProperty().bind(label.heightProperty().divide(-2))

        return pane
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