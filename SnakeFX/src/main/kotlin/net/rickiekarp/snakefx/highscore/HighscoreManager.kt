package net.rickiekarp.snakefx.highscore

import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import net.rickiekarp.core.AppContext
import net.rickiekarp.snakefx.net.SnakeNetworkApi

import java.util.ArrayList
import java.util.Date

import net.rickiekarp.snakefx.settings.Config.*

/**
 * The purpose of the HighscoreManager is to add new highscore entries and to
 * verify that there are only as many entries in the highscore list as defined
 * in [net.rickiekarp.snakefx.config.Config.MAX_SCORE_COUNT].
 */
class HighscoreManager {

    private val highScoreEntries = SimpleListProperty(
            ObservableListWrapper(ArrayList<HighScoreEntry>()))

    fun highScoreEntries(): ListProperty<HighScoreEntry> {
        return highScoreEntries
    }

    fun addScore(name: String, points: Int) {
        val entry = HighScoreEntry(name, points, Date())
        val response = AppContext.context.networkApi.requestResponse(SnakeNetworkApi.requestAddHighscore(entry))
        if (response!!.code == 200) {
            highScoreEntries.add(entry)
            updateList()
        }
    }

    fun isNameValid(name: String?): Boolean {
        if (name == null) {
            return false
        }
        if (name.isEmpty()) {
            return false
        }
        return if (name.contains(",")) {
            false
        } else !name.contains(";")
    }

    private fun updateList() {
        FXCollections.sort(highScoreEntries)
        for (i in highScoreEntries.indices) {
            if (i < MAX_SCORE_COUNT.get()) {
                highScoreEntries[i].ranking = i + 1
            } else {
                highScoreEntries.removeAt(i)
            }
        }
    }
}
