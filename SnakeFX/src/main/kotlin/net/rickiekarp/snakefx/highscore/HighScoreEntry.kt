package net.rickiekarp.snakefx.highscore

import org.codehaus.jackson.annotate.JsonIgnore

import java.util.Date

class HighScoreEntry : Comparable<HighScoreEntry> {

    @JsonIgnore
    var ranking: Int = 0

    var id: Int = 0
    var name: String? = null
    var points: Int = 0
    var dateAdded: Date? = null

    // used for serialization. do not remove
    constructor() {}

    constructor(playername: String, points: Int, dateAdded: Date) {
        this.name = playername
        this.points = points
        this.dateAdded = dateAdded
    }

    override fun compareTo(o: HighScoreEntry): Int {
        return Integer.compare(o.points, this.points)
    }

    override fun toString(): String {
        return "$name->$points points"
    }
}
