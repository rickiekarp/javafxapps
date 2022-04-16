package net.rickiekarp.snakefx.core

/**
 * This enum represents the directions that the snake can go.
 */
enum class Direction private constructor(private val horizontal: Boolean) {

    UP(false),

    DOWN(false),

    LEFT(true),

    RIGHT(true);

    fun hasSameOrientation(other: Direction?): Boolean {
        return if (other == null) {
            false
        } else horizontal == other.horizontal
    }
}
