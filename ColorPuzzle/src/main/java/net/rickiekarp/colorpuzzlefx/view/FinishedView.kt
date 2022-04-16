package net.rickiekarp.colorpuzzlefx.view

import de.saxsys.mvvmfx.FxmlView
import de.saxsys.mvvmfx.InjectViewModel
import javafx.fxml.FXML

class FinishedView : FxmlView<FinishedViewModel?> {
    @InjectViewModel
    private val viewModel: FinishedViewModel? = null

    @FXML
    fun newGame() {
        viewModel!!.newGame()
    }
}