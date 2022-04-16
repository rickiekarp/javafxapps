package net.rickiekarp.snakefx.core

import net.rickiekarp.snakefx.view.ViewModel
import javafx.animation.Animation.Status

import java.util.function.Consumer

/**
 * The purpose of this function is to start a new Game.
 */
class NewGameFunction(private val viewModel: ViewModel, private val grid: Grid, private val snake: Snake,
                      private val foodGenerator: FoodGenerator) : Consumer<Void?> {

    override fun accept(aVoid: Void?) {
        viewModel.gameloopStatus.set(Status.STOPPED)

        grid.newGame()

        snake.newGame()

        foodGenerator.generateFood()

        viewModel.gameloopStatus.set(Status.RUNNING)
        viewModel.gameloopStatus.set(Status.PAUSED)
    }
}
