package net.rickiekarp.botter.listcell

import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.botlib.plugin.BotSetting
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.shape.SVGPath

class FoldableListCell(private val list: ListView<*>) : ListCell<BotSetting>() {

    override fun updateItem(item: BotSetting, empty: Boolean) {
        super.updateItem(item, empty)
        if (!empty) {

            val vbox = VBox()
            graphic = vbox

            //title
            val labelHeader = Label(item.title)
            labelHeader.graphicTextGap = 10.0
            labelHeader.id = "tableview-columnheader-default-bg"
            labelHeader.prefWidth = list.width - 40
            labelHeader.prefHeight = 30.0
            if (DebugHelper.DEBUGVERSION) {
                labelHeader.style = "-fx-background-color: gray;"
            }

            labelHeader.graphic = createArrowPath(30, !item.isVisible)

            //description
            val labelDescription = Label(item.desc)
            labelDescription.isWrapText = true
            labelDescription.style = "-fx-font-size: 9pt;"

            //node
            val settingVBox = item.node

            vbox.children.add(labelHeader)
            if (item.isVisible) {
                vbox.children.addAll(labelDescription, settingVBox)
            }

            labelHeader.setOnMouseEntered {
                labelHeader.style = "-fx-background-color: derive(-fx-base, 5%);"
                settingVBox!!.style = "-fx-background-color: derive(-fx-base, 5%);"
            }
            labelHeader.setOnMouseExited {
                labelHeader.style = null
                settingVBox!!.style = null
            }

            labelHeader.setOnMouseClicked {
                if (item.isVisible) {
                    labelHeader.graphic = createArrowPath(30, true)
                    for (i in vbox.children.size - 1 downTo 1) {
                        vbox.children.removeAt(i)
                    }
                } else {
                    labelHeader.graphic = createArrowPath(30, false)
                    vbox.children.add(labelDescription)
                    vbox.children.add(item.node)
                }
                item.isVisible = !item.isVisible
            }

        } else {
            text = null
            graphic = null
        }
    }


    private fun createArrowPath(height: Int, up: Boolean): SVGPath {
        val svg = SVGPath()
        val width = height / 4
        if (up) {
            svg.content = "M" + width + " 0 L" + width * 2 + " " + width + " L0 " + width + " Z"
            svg.stroke = Paint.valueOf("white")
            svg.fill = Paint.valueOf("white")
        } else {
            svg.content = "M0 0 L" + width * 2 + " 0 L" + width + " " + width + " Z"
            svg.stroke = Paint.valueOf("white")
            svg.fill = Paint.valueOf("white")
        }
        return svg
    }
}
