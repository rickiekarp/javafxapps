package net.rickiekarp.core.ui.windowmanager

import net.rickiekarp.core.components.button.SidebarButton
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

import java.util.ArrayList

class WindowContentController internal constructor() {
    private val sidebarNodes: MutableList<SidebarButton>
    var titlebarButtonBox: HBox? = null
        private set
    var sidebarButtonBox: VBox? = null

    val list: List<SidebarButton>
        get() = sidebarNodes

    init {
        sidebarNodes = ArrayList(3)
    }

    fun addSidebarItem(position: Int, node: SidebarButton) {
        sidebarNodes.add(position, node)
    }

    fun addSidebarItem(node: SidebarButton) {
        addSidebarItem(sidebarNodes.size, node)
    }

    /**
     * Removes a SidebarButton item by its given identifier
     * @param identifier SidebarButton identifier
     */
    fun removeSidebarItemByIdentifier(identifier: String) {
        for (i in sidebarNodes.indices) {
            if (sidebarNodes[i].identifier == identifier) {
                sidebarNodes.removeAt(i)
            }
        }
        sidebarButtonBox!!.children.setAll(sidebarNodes)
    }

    fun setTitlebarRightButtonBox(titlebar: HBox) {
        this.titlebarButtonBox = titlebar
    }
}
