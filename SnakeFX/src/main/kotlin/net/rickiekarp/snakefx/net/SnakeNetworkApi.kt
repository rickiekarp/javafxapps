package net.rickiekarp.snakefx.net

import net.rickiekarp.core.net.NetworkAction
import net.rickiekarp.core.net.NetworkApi
import net.rickiekarp.core.net.provider.NetworkParameterProvider
import net.rickiekarp.snakefx.highscore.HighScoreEntry

class SnakeNetworkApi : NetworkApi() {
    companion object {
        private val GAME_API_CONTEXT = "gamedata"
        private val RANKING_DOMAIN = "ranking"
        private val RANKING_GET_ACTION = "highscore"
        private val RANKING_ADD_ACTION = "addHighscore"

        fun requestRanking(): NetworkAction {
            return NetworkAction.Builder.create().setHost(GAME_API_CONTEXT).setDomain(RANKING_DOMAIN).setAction(RANKING_GET_ACTION).setMethod("GET").build()
        }

        fun requestAddHighscore(entry: HighScoreEntry): NetworkAction {
            val provider = NetworkParameterProvider.create()
            provider.put("name", entry.name)
            provider.put("points", entry.points)
            return NetworkAction.Builder.create().setHost(GAME_API_CONTEXT).setDomain(RANKING_DOMAIN).setAction(RANKING_ADD_ACTION).setParameters(provider).setMethod("POST").build()
        }
    }
}
