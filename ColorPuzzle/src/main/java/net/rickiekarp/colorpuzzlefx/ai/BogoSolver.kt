package net.rickiekarp.colorpuzzlefx.ai

import net.rickiekarp.colorpuzzlefx.core.Colors
import net.rickiekarp.colorpuzzlefx.core.GameLogic
import java.util.*

class BogoSolver : Solver {
    private var game: GameLogic? = null
    private var allColors: List<Colors>? = null
    override fun setGame(game: GameLogic) {
        this.game = game
        allColors = ArrayList(game.profile.profile.keys)
    }

    override fun nextStep(): Colors {
        val currentColor = game!!.currentColor
        Collections.shuffle(allColors)
        return allColors!!.stream()
                .filter { color: Colors -> color != currentColor }
                .findAny().orElse(currentColor)
    }
}