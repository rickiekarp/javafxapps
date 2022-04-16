package net.rickiekarp.snakefx.highscore

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.SerializationConfig.Feature
import org.codehaus.jackson.map.type.TypeFactory

import java.io.IOException
import java.util.Collections

/**
 * DAO implementation for [HighScoreEntry] that is using a JSON for persistence.
 */
class HighscoreJsonDao : HighscoreDao {

    private val mapper: ObjectMapper
    private val typeFactory: TypeFactory

    init {
        mapper = ObjectMapper()
        mapper.configure(Feature.INDENT_OUTPUT, true)
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
