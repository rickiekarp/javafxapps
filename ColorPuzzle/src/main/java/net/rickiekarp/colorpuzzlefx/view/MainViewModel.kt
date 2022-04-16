package net.rickiekarp.colorpuzzlefx.view

import de.saxsys.mvvmfx.ViewModel
import eu.lestard.grid.GridModel
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.scene.paint.Color
import net.rickiekarp.colorpuzzlefx.core.ColorProfile
import net.rickiekarp.colorpuzzlefx.core.Colors
import net.rickiekarp.colorpuzzlefx.core.GameLogic
import net.rickiekarp.colorpuzzlefx.view.ai.solver.SolverViewPopup

class MainViewModel(val gameLogic: GameLogic) : ViewModel {
    private val profile = ColorProfile()
    private val movesLabelText: StringProperty = SimpleStringProperty()
    private val gameFinished: BooleanProperty = SimpleBooleanProperty()
    fun newGameAction() {
        gameLogic.newGame()
        gameFinished.set(false)
    }

    fun selectColorAction(color: Colors?) {
        gameLogic.selectColor(color)
    }

    val colorMappings: Map<Colors, Color>
        get() = profile.getProfile()

    fun movesLabelText(): ReadOnlyStringProperty {
        return movesLabelText
    }

    fun gameFinished(): ReadOnlyBooleanProperty {
        return gameFinished
    }

    val gridModel: GridModel<Colors>
        get() = gameLogic.gridModel

    fun openAI() {
        SolverViewPopup.open()
    }

    init {
        movesLabelText.bind(Bindings.concat("Moves:", gameLogic.movesCounter()))
        newGameAction()
        gameLogic.onFinished { gameFinished.value = true }
    }
}