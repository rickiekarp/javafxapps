package net.rickiekarp.snakefx.core

import javafx.scene.paint.Color

/**
 * Represents the states that a field can has.
 *
 * Every state is connected to a [Color] that the field will get when it
 * is in the given state.
 */
enum class State constructor(
        /**
         * @return the color of the State.
         */
        val color: Color) {

    EMPTY(Color.WHITE),

    HEAD(Color.DARKGREEN),

    TAIL(Color.FORESTGREEN),

    FOOD(Color.BLACK)

}