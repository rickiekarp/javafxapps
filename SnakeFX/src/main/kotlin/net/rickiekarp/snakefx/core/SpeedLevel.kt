package net.rickiekarp.snakefx.core

/**
 * Represents the different levels of speed for the game loop. The speed is
 * stored as Frames per Second.
 */
enum class SpeedLevel private constructor(val fps: Int) {
    EASY(5),

    MEDIUM(10),

    HARD(15),

    INSANE(25)

}