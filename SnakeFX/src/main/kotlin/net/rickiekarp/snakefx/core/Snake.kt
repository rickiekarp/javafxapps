package net.rickiekarp.snakefx.core

import javafx.beans.value.ObservableValue
import net.rickiekarp.snakefx.settings.Config
import net.rickiekarp.snakefx.view.ViewModel
import java.util.*
import java.util.function.Consumer

/**
 * This class represents the snake.
 */
class Snake(private val viewModel: ViewModel, private val grid: Grid, gameLoop: GameLoop) {
    var head: GameField? = null
        private set
    private val x: Int
    private val y: Int
    var currentDirection: Direction? = null
        private set
    var nextDirection: Direction? = null
        private set
    private val tail: MutableList<GameField?>

    /**
     * Initalizes the fields of the snake.
     */
    fun init() {
        setHead(grid.getXY(x, y))
        viewModel.collision.set(false)
        viewModel.points.set(0)
        currentDirection = Direction.UP
        nextDirection = Direction.UP
    }

    /**
     * Change the direction of the snake. The direction is only changed when the new direction has <bold>not</bold> the
     * same orientation as the old one.
     *
     *
     * For example, when the snake currently has the direction UP and the new direction should be DOWN, nothing will
     * happend because both directions are vertical.
     *
     *
     * This is to prevent the snake from moving directly into its own tail.
     *
     * @param newDirection
     */
    private fun changeDirection(newDirection: Direction) {
        if (!newDirection.hasSameOrientation(currentDirection)) {
            nextDirection = newDirection
        }
    }

    /**
     * Move the snake by one field.
     */
    fun move() {
        currentDirection = nextDirection

        // prevent snake direction from being different than the current direction
        if (viewModel.snakeDirection.get() !== currentDirection) {
            viewModel.snakeDirection.set(currentDirection)
        }
        val newHead = grid.getFromDirection(head!!, currentDirection!!)
        if (newHead.state == State.TAIL) {
            viewModel.collision.set(true)
            return
        }
        var grow = false
        if (newHead.state == State.FOOD) {
            grow = true
        }
        var lastField = head
        for (i in tail.indices) {
            val f = tail[i]
            lastField!!.changeState(State.TAIL)
            tail[i] = lastField
            lastField = f
        }
        if (grow) {
            grow(lastField)
            addPoints()
        } else {
            lastField!!.changeState(State.EMPTY)
        }
        setHead(newHead)
    }

    fun newGame() {
        tail.clear()
        init()
    }

    private fun setHead(head: GameField) {
        this.head = head
        head.changeState(State.HEAD)
    }

    /**
     * The given field is added to the tail of the snake and gets the state TAIL.
     *
     * @param field
     */
    private fun grow(field: GameField?) {
        field!!.changeState(State.TAIL)
        tail.add(field)
    }

    private fun addPoints() {
        val current = viewModel.points.get()
        viewModel.points.set(current + viewModel.pointIncrement.get())
    }

    fun getTail(): List<GameField?> {
        return tail
    }

    /**
     * @param viewModel the viewModel
     * @param grid      the grid on which the snake is created
     * @param gameLoop  the gameloop that is used for the movement of the snake
     */
    init {
        x = Config.SNAKE_START_X.get()
        y = Config.SNAKE_START_Y.get()
        tail = ArrayList()
        gameLoop.addActions(Consumer { x: Any? -> move() })
        viewModel.snakeDirection.addListener { observable: ObservableValue<out Direction>?, oldDirection: Direction?, newDirection: Direction -> changeDirection(newDirection) }
    }
}