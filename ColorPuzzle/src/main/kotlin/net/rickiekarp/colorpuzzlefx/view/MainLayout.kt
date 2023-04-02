package net.rickiekarp.colorpuzzlefx.view

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import net.rickiekarp.core.view.layout.AppLayout

class MainLayout(private val parent: Parent) : AppLayout {
    override val layout: Node
        get() {

            val borderpane = BorderPane()

            borderpane.center = parent
//            borderpane.right = settingsGrid
//            borderpane.top = fxmlFactory.getFxmlRoot(FXMLFile.PANEL)
//            borderpane.top = getPanel()

            return borderpane
        }

    override fun postInit() {
        // do nothing
    }

}
