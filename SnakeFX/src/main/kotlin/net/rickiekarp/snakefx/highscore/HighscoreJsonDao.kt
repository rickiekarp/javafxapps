package net.rickiekarp.snakefx.highscore

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.type.TypeFactory
import java.io.IOException

/**
 * DAO implementation for [HighScoreEntry] that is using a JSON for persistence.
 */
class HighscoreJsonDao : HighscoreDao {

    private val mapper: ObjectMapper = ObjectMapper()
    private val typeFactory: TypeFactory

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        typeFactory = TypeFactory.defaultInstance()
    }

    override fun load(jsonString: String): List<HighScoreEntry> {
        return try {
            mapper.readValue(jsonString, typeFactory.constructCollectionType(List::class.java, HighScoreEntry::class.java))
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }
}
