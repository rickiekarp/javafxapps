package net.rickiekarp.core.components

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.model.SettingEntry
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.shape.SVGPath

class FoldableListCell(private var list: ListView<SettingEntry>?) : ListCell<SettingEntry>() {

    override fun updateItem(item: SettingEntry?, empty: Boolean) {
        super.updateItem(item, empty)

        if (!empty) {
            val vbox = VBox()
            graphic = vbox

            val labelHeader = Label(LanguageController.getString(item!!.title!!))
            labelHeader.graphicTextGap = 10.0
            labelHeader.id = "tableview-columnheader-default-bg"
            labelHeader.prefWidth = list!!.width - 40
            labelHeader.prefHeight = 30.0
            if (DebugHelper.DEBUGVERSION) {
                labelHeader.style = "-fx-background-color: gray;"
            }

            vbox.children.add(labelHeader)

            labelHeader.graphic = createArrowPath(30, !item.isHidden)

            labelHeader.setOnMouseEntered {
                labelHeader.style = "-fx-background-color: derive(-fx-base, 5%);"
                item.content.style = "-fx-background-color: derive(-fx-base, 5%);"
            }
            labelHeader.setOnMouseExited {
                labelHeader.style = null
                item.content.style = null
            }
            labelHeader.setOnMouseClicked {
                item.isHidden = !item.isHidden
                if (item.isHidden) {
                    labelHeader.graphic = createArrowPath(30, false)
                    for (i in 1 until vbox.children.size) {
                        vbox.children.removeAt(i)
                    }
                } else {
                    labelHeader.graphic = createArrowPath(30, true)
                    vbox.children.add(getItem().content)
                }
            }

            if (!item.isHidden) {
                vbox.children.add(item.content)
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
