package net.rickiekarp.snakefx.settings

/**
 * This enum represents configuration parameters.
 */
enum class Config constructor(private val value: Int) {

    /**
     * The number of rows and columns of the grid. In this game the grid is a
     * square and so the number of rows and columns are equal.
     */
    ROW_AND_COLUMN_COUNT(20),

    /**
     * The size of the grid in pixel.
     */
    GRID_SIZE_IN_PIXEL(500),

    /**
     * The x coordinate of the starting point of the snake
     */
    SNAKE_START_X(10),

    /**
     * The y coordinate of the starting point of the snake
     */
    SNAKE_START_Y(10),

    /**
     * The max number of HighScore entries that are saved and persisted
     */
    MAX_SCORE_COUNT(50);

    /**
     * @return the configuration value of this enum constant.
     */
    fun get(): Int {
        return value
    }
}