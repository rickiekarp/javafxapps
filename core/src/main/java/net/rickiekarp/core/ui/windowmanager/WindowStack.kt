package net.rickiekarp.core.ui.windowmanager

import net.rickiekarp.core.view.MainScene
import javafx.scene.Node
import javafx.scene.control.Button

import java.util.Stack

class WindowStack : Stack<Node>() {
    private var backButton: Button? = null

    override fun push(item: Node): Node {
        //System.out.println("Push " + item);
        addElement(item)
        MainScene.mainScene.borderPane.center = item
        if (this.size == 2) {
            initializeBackButton()
            MainScene.mainScene.windowScene!!.win.titlebarButtonBox.children.add(backButton)
        }
        return item
    }

    override fun pop(): Node {
        //System.out.print("pop -> " + this.peek());
        return super.pop()
    }

    private fun initializeBackButton() {
        backButton = Button("Back")
        backButton!!.setOnAction { event ->
            this.pop()
            MainScene.mainScene.borderPane.center = super.peek()
            if (this.size == 1) {
                MainScene.mainScene.windowScene!!.win.titlebarButtonBox.children.remove(backButton)
                backButton = null
            }
        }
    }
}
