package net.rickiekarp.flc;

import javafx.stage.Stage;
import net.rickiekarp.core.AppStarter;
import net.rickiekarp.core.controller.AppLaunch;
import net.rickiekarp.flc.controller.FileCommands;
import net.rickiekarp.flc.settings.AppConfiguration;
import net.rickiekarp.flc.view.layout.MainLayout;

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

        setWinType((byte) 0);
        setMinWidth(800);
        setMinHeight(600);
        setWidth(1024);
        setHeight(768);
        makeResizable();
        setLayout(new MainLayout());

        super.start(stage);
        postLaunch();
    }

    @Override
    public void postLaunch() {
        try {
            new FileCommands().addFileCommands();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}