package net.rickiekarp.colorpuzzlefx;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import eu.lestard.easydi.EasyDI;
import javafx.stage.Stage;
import net.rickiekarp.colorpuzzlefx.ai.BogoSolver;
import net.rickiekarp.colorpuzzlefx.ai.Solver;
import net.rickiekarp.colorpuzzlefx.settings.AppConfiguration;
import net.rickiekarp.colorpuzzlefx.view.MainLayout;
import net.rickiekarp.colorpuzzlefx.view.MainView;
import net.rickiekarp.colorpuzzlefx.view.MainViewModel;
import net.rickiekarp.core.AppStarter;

public class MainApp extends AppStarter {

    /**
     * Main Method
     * @param args Program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        setMainClazz(MainApp.class);
        setConfigClazz(AppConfiguration.class);

        setWinType((byte) 1);
        setMinWidth(700);
        setMinHeight(620);
        setWidth(700);
        setHeight(650);

        EasyDI context = new EasyDI();
        context.bindInterface(Solver.class, BogoSolver.class);

        MvvmFX.setCustomDependencyInjector(context::getInstance);
        final ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();

        setLayout(new MainLayout(viewTuple.getView()));

        super.start(stage);
    }
}
