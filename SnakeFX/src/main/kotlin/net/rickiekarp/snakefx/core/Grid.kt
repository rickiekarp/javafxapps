package net.rickiekarp.snakefx.core

import com.sun.javafx.collections.ObservableListWrapper
import net.rickiekarp.snakefx.settings.Config
import net.rickiekarp.snakefx.view.ViewModel

import java.util.ArrayList
import java.util.Collections
import java.util.Random

import net.rickiekarp.snakefx.settings.Config.*

/**
 * This class is the grid of the game. It contains a collection of [GameField]
 * instances.
 */
open class Grid {

    private val gridSizeInPixel = GRID_SIZE_IN_PIXEL.get()

    private val fields = ObservableListWrapper(ArrayList<GameField>())

    // filter the list to only empty fields.
    open val randomEmptyField: GameField?
        get() {
            val emptyFields = fields.filter { it.state == State.EMPTY }.map { it }

            return if (emptyFields.isEmpty()) {
                null
            } else {
                val nextInt = Random().nextInt(emptyFields.size)
                emptyFields[nextInt]
            }
        }

    /**
     * This method initializes the grid. According to the
     * [ViewModel.gridSize] the fields ([GameField]) are
     * created with the coordinates and the size that is calculated with the
     * value of [Config.GRID_SIZE_IN_PIXEL].
     *
     */
    fun init() {
        val gridSize = ROW_AND_COLUMN_COUNT.get()

        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                fields.add(GameField(x, y, gridSizeInPixel / gridSize))
            }
        }
    }

    /**
     * @return an unmodifiable list of all fields.
     */
    fun getFields(): List<GameField> {
        return Collections.unmodifiableList(fields)
    }

    /**
     *
     * @param x
     * the x coordinate
     * @param y
     * the y coordinate
     * @return the field with the given coordinates or null if no field with
     * this coordinates is available.
     */
    open fun getXY(x: Int, y: Int): GameField {
        return fields.stream()
                .filter { field -> field.x == x && field.y == y }
                .findFirst()
                .orElse(null)
    }

    /**
     * returns the field that is located next to the given field in the given
     * direction.
     *
     * @param field
     * @param direction
     * @return the field in the given direction
     */
    fun getFromDirection(field: GameField, direction: Direction): GameField {
        var x = field.x
        var y = field.y

        when (direction) {
            Direction.DOWN -> y += 1
            Direction.LEFT -> x -= 1
            Direction.RIGHT -> x += 1
            Direction.UP -> y -= 1
        }

        val gridSize = ROW_AND_COLUMN_COUNT.get()

        x += gridSize
        y += gridSize
        x = x % gridSize
        y = y % gridSize

        return getXY(x, y)
    }

    fun newGame() {
        fields.forEach { field -> field.changeState(State.EMPTY) }
    }
}
