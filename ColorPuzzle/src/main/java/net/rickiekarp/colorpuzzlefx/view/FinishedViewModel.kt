package net.rickiekarp.colorpuzzlefx.view

import de.saxsys.mvvmfx.ViewModel
import java.util.function.Consumer

class FinishedViewModel : ViewModel {
    private var onNewGame: Consumer<Void?>? = null
    fun newGame() {
        if (onNewGame != null) {
            onNewGame!!.accept(null)
        }
    }

    fun setOnNewGame(onNewGame: Consumer<Void?>?) {
        this.onNewGame = onNewGame
    }
}