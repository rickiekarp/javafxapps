package net.rickiekarp.snakefx.util

import net.rickiekarp.snakefx.core.Direction
import net.rickiekarp.snakefx.view.ViewModel
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

/**
 * This class handles the input of the user.
 */
class KeyboardHandler(private val viewModel: ViewModel) : EventHandler<KeyEvent> {

    // also add WASD keys to keyboard handler due to https://bugs.openjdk.java.net/browse/JDK-8215545
    override fun handle(event: KeyEvent) {
        when (event.code) {
            KeyCode.W, KeyCode.UP -> viewModel.snakeDirection.set(Direction.UP)
            KeyCode.A, KeyCode.LEFT -> viewModel.snakeDirection.set(Direction.LEFT)
            KeyCode.S, KeyCode.DOWN -> viewModel.snakeDirection.set(Direction.DOWN)
            KeyCode.D, KeyCode.RIGHT -> viewModel.snakeDirection.set(Direction.RIGHT)
        }
    }
}