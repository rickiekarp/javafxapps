package net.rickiekarp.snakefx.highscore

/**
 * Interface for Data Access Object implementations to handle the persistence of
 * [HighScoreEntry] instances.
 */
interface HighscoreDao {
    fun load(jsonString: String): List<HighScoreEntry>
}
