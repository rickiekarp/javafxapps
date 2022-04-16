package net.rickiekarp.snakefx.core

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * This class represents a single field in the [Grid] of the game.
 */
open class GameField
/**
 * Creates a new Field with the given sizeInPixel at the location specified
 * by x and y coordinate.
 *
 * x and y are coordinates in the coordinate system of the game [Grid]
 * . They are <bold>not</bold> representing the coordinates in pixel of the
 * underlying rectangle in the JavaFX canvas.
 *
 * The pixel-x and pixel-y coordinates of the underlying rectangle is
 * calculated from the given x and y coordinate and the sizeInPixel.
 *
 * @param x
 * @param y
 * @param sizeInPixel
 */
(
        /**
         * The x coordinate of the field in the game's grid.
         *
         * @return the x coordinate.
         */
        val x: Int,
        /**
         * The y coordinate of the field in the game's grid.
         *
         * @return y coordinate.
         */
        val y: Int, sizeInPixel: Int) {

    /**
     * @return the underlying JavaFX [Rectangle].
     */
    val rectangle: Rectangle

    /**
     * @return the current state of the field.
     */
    var state: State? = null
        private set

    init {

        state = State.EMPTY

        rectangle = Rectangle((x * sizeInPixel).toDouble(), (y * sizeInPixel).toDouble(),
                sizeInPixel.toDouble(), sizeInPixel.toDouble())

        //rectangle.setStroke(Color.LIGHTGRAY);
        rectangle.fill = Color.WHITE

    }

    /**
     * The given state is set as the state of the field and the fill color of
     * the rectangle of this field is changed to the color of the given state.
     *
     * @param newState
     */
    fun changeState(newState: State) {
        state = newState

        rectangle.fill = newState.color
    }
}
