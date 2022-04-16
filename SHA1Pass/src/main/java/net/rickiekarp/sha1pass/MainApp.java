package net.rickiekarp.sha1pass;

import javafx.stage.Stage;
import net.rickiekarp.core.AppStarter;
import net.rickiekarp.sha1pass.settings.AppConfiguration;
import net.rickiekarp.sha1pass.view.MainLayout;

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
        setMinWidth(440);
        setMinHeight(145);
        setWidth(475);
        setHeight(205);
        setLayout(new MainLayout());

        super.start(stage);
    }
}
