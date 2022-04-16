package net.rickiekarp.colorpuzzlefx

import de.saxsys.mvvmfx.FluentViewLoader
import de.saxsys.mvvmfx.MvvmFX
import eu.lestard.easydi.EasyDI
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import net.rickiekarp.colorpuzzlefx.ai.BogoSolver
import net.rickiekarp.colorpuzzlefx.ai.Solver
import net.rickiekarp.colorpuzzlefx.view.MainView

class App : Application() {
    @Throws(Exception::class)
    override fun start(stage: Stage) {
        stage.title = "ColorPuzzleFX"
        val context = EasyDI()
        context.bindInterface(Solver::class.java, BogoSolver::class.java)
        MvvmFX.setCustomDependencyInjector { requestedType: Class<*>? -> context.getInstance(requestedType) }
        val viewTuple = FluentViewLoader.fxmlView(MainView::class.java).load()
        stage.scene = Scene(viewTuple.view)
        stage.sizeToScene()
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(*args)
        }
    }
}