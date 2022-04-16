package net.rickiekarp.qaacc;

import javafx.stage.Stage;
import net.rickiekarp.core.AppStarter;
import net.rickiekarp.core.controller.AppLaunch;
import net.rickiekarp.qaacc.settings.AppConfiguration;
import net.rickiekarp.qaacc.view.MainLayout;

public class MainApp extends AppStarter implements AppLaunch {

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
        setMinWidth(500);
        setMinHeight(300);
        setWidth(750);
        setHeight(335);
        setLayout(new MainLayout());

        super.start(stage);
        postLaunch();
    }

    @Override
    public void postLaunch() {

    }
}