package net.rickiekarp.sha1pass.scenes

import javafx.beans.binding.ObjectExpression
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.shape.Circle
import javafx.stage.Stage
import net.rickiekarp.core.components.textfield.CustomTextField
import net.rickiekarp.core.provider.LocalizationProvider
import net.rickiekarp.core.ui.windowmanager.ImageLoader
import net.rickiekarp.core.ui.windowmanager.WindowScene
import net.rickiekarp.core.ui.windowmanager.WindowStage
import net.rickiekarp.core.util.ClipboardUtil
import net.rickiekarp.core.util.crypt.HexCoder
import net.rickiekarp.core.util.crypt.SHA1Coder
import net.rickiekarp.core.view.MainScene

/**
 * About Stage GUI.
 */
class CoordinatesScene {

    private lateinit var series: Series<Number, Number>
    private var viewTag = "location"

    private val content: BorderPane
        get() {

            val borderpane = BorderPane()
            borderpane.style = "-fx-background-color: #1d1d1d;"

            val xAxis = NumberAxis()
            val yAxis = NumberAxis()

            series = Series<Number, Number>()

            val chart = LineChart(xAxis, yAxis)
            chart.isLegendVisible = false
            chart.data.add(series)

            val nodes: Set<Node> = chart.lookupAll(".series0")
            nodes.first().style = "-fx-stroke: transparent;"

            val root: Pane = StackPane(chart)

            borderpane.top = getControls()
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

    private fun getControls() : GridPane {
        val controls = GridPane()
        controls.padding = Insets(10.0, 7.0, 10.0, 7.0)
        controls.alignment = Pos.CENTER
        controls.vgap = 10.0
        controls.hgap = 10.0

        // Point A

        val fieldX = CustomTextField()
        fieldX.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldX, 0, 0)
        val fieldY = CustomTextField()
        fieldY.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldY, 1, 0)

        fieldX.onKeyTyped = EventHandler { validateCoordinates(fieldX, fieldY, "red") }
        fieldY.onKeyTyped = EventHandler { validateCoordinates(fieldX, fieldY, "red") }

        // Point B

        val fieldBX = CustomTextField()
        fieldBX.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldBX, 0, 1)
        val fieldBY = CustomTextField()
        fieldBY.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldBY, 1, 1)

        fieldBX.onKeyTyped = EventHandler { validateCoordinates(fieldBX, fieldBY, "red") }
        fieldBY.onKeyTyped = EventHandler { validateCoordinates(fieldBX, fieldBY, "red") }

        // Point C

        val fieldCX = CustomTextField()
        fieldCX.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldCX, 3, 0)
        val fieldCY = CustomTextField()
        fieldCY.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldCY, 4, 0)

        fieldCX.onKeyTyped = EventHandler { validateCoordinates(fieldCX, fieldCY, "orange") }
        fieldCY.onKeyTyped = EventHandler { validateCoordinates(fieldCX, fieldCY, "orange") }

        // Point D

        val fieldDX = CustomTextField()
        fieldDX.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldDX, 3, 1)
        val fieldDY = CustomTextField()
        fieldDY.setRestrict("-?[0-9]")
        GridPane.setConstraints(fieldDY, 4, 1)

        fieldDX.onKeyTyped = EventHandler { validateCoordinates(fieldDX, fieldDY, "orange") }
        fieldDY.onKeyTyped = EventHandler { validateCoordinates(fieldDX, fieldDY, "orange") }

        // controls

        val result = Label()
        GridPane.setConstraints(result, 3, 2)

        val button = Button(LocalizationProvider.getString("calculate"))
        button.setOnAction {
            val addedIntersection = calcResult(fieldX, fieldY, fieldBX, fieldBY, fieldCX, fieldCY, fieldDX, fieldDY)
            if (addedIntersection != null) {
                val intersectionNode = XYChart.Data<Number,Number>(addedIntersection.first, addedIntersection.second)
                intersectionNode.node = createDataNode(intersectionNode.XValueProperty(), intersectionNode.YValueProperty())
                intersectionNode.node.style = "-fx-background-color: green, green;"
                series.data.add(intersectionNode)

                val sha1 = SHA1Coder.getSHA1Bytes(addedIntersection.first.toString() + "," + addedIntersection.second.toString(), false)
                ClipboardUtil.setStringToClipboard(HexCoder.bytesToHex(sha1))
                result.text = LocalizationProvider.getString("copiedToClipboard")
            }
        }
        GridPane.setConstraints(button, 2, 2)

        controls.children.addAll(
            fieldX, fieldY,
            fieldBX, fieldBY,
            fieldCX, fieldCY,
            fieldDX, fieldDY,
            button, result
        )
        return controls
    }

    private fun calcResult(
        fieldX: CustomTextField, fieldY: CustomTextField,
        fieldBX: CustomTextField, fieldBY: CustomTextField,
        fieldCX: CustomTextField, fieldCY: CustomTextField,
        fieldDX: CustomTextField, fieldDY: CustomTextField
    ) : Pair<Double, Double>? {
        val formula = calcLinearFunctionFormula(
            Point2D(fieldX.getDoubleValue(), fieldY.getDoubleValue()),
            Point2D(fieldBX.getDoubleValue(), fieldBY.getDoubleValue()),
        )
        val formulaB = calcLinearFunctionFormula(
            Point2D(fieldCX.getDoubleValue(), fieldCY.getDoubleValue()),
            Point2D(fieldDX.getDoubleValue(), fieldDY.getDoubleValue()),
        )
        return calcIntersectionPoint(formula, formulaB)
    }

    private fun validateCoordinates(fieldX: CustomTextField, fieldY: CustomTextField, color: String) {
        if (fieldX.text.isNotEmpty() && fieldY.text.isNotEmpty()) {
            val a = fieldX.getDoubleValue()
            val b = fieldY.getDoubleValue()

            val data1 = XYChart.Data<Number,Number>(a, b)
            data1.node = createDataNode(data1.XValueProperty(), data1.YValueProperty())
            data1.node.style = "-fx-background-color: $color, $color;"

            series.data.add(data1)
        }
    }
}