package net.rickiekarp.snakefx.core

import net.rickiekarp.snakefx.view.ViewModel


/**
 * This class generates new food that the snake can eat.
 *
 * "Food" means that the given field gets the state [State.FOOD].
 *
 * The food is generated at an empty field at a random location.
 */
class FoodGenerator(viewModel: ViewModel, private val grid: Grid) {

    init {

        viewModel.points.addListener { observable, oldValue, newValue ->
            if (oldValue.toInt() < newValue.toInt()) {
                generateFood()
            }
        }
    }

    /**
     * Generates new food.
     */
    fun generateFood() {
        val field = grid.randomEmptyField

        field!!.changeState(State.FOOD)
    }
}