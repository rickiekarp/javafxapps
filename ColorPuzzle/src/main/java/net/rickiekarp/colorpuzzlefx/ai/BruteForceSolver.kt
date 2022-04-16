package net.rickiekarp.colorpuzzlefx.ai

import net.rickiekarp.colorpuzzlefx.core.Colors
import net.rickiekarp.colorpuzzlefx.core.GameLogic

class BruteForceSolver : Solver {
    private var game: GameLogic? = null
    private var indexLastColor = 0
    override fun setGame(game: GameLogic) {
        this.game = game
    }

    override fun nextStep(): Colors {
        val nextColor = Colors.values()[indexLastColor]
        if (indexLastColor + 1 < Colors.values().size) {
            indexLastColor++
        } else {
            indexLastColor = 0
        }
        return nextColor
    }
}