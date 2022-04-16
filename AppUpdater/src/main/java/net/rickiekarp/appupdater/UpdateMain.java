package net.rickiekarp.appupdater;

import net.rickiekarp.appupdater.ui.UpdateCheckerGUI;
import net.rickiekarp.appupdater.updatemanager.UpdateInstaller;
import javafx.application.Application;
import javafx.stage.Stage;

public class UpdateMain extends Application {
    private static String[] savedArgs;
    public static String[] getArgs() {
        return savedArgs;
    }

    public static void main(String[] args) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        savedArgs = args;
        launch(args);
    }

    public void start(final Stage stage) {
        try {
            switch (savedArgs[0]) {
                case "update":
                    if (savedArgs.length > 1 && savedArgs[1].endsWith(".jar")) {
                        UpdateInstaller installer = new UpdateInstaller();
                        installer.installFiles();
                    } else {
                        UpdateCheckerGUI.Companion.setUpdateChecker(new UpdateCheckerGUI());
                        UpdateCheckerGUI.Companion.getUpdateChecker().setMessage("Jar to update has not been defined! Check your program arguments!");
                    }
                    break;
                case "check":
                    UpdateCheckerGUI.Companion.setUpdateChecker(new UpdateCheckerGUI());
                    UpdateCheckerGUI.Companion.getUpdateChecker().setMessage("Checking for updates...");
                    break;
                default:
                    UpdateCheckerGUI.Companion.setUpdateChecker(new UpdateCheckerGUI());
                    UpdateCheckerGUI.Companion.getUpdateChecker().setMessage("Parameter invalid! Please try again with a valid argument!");
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            UpdateCheckerGUI.Companion.setUpdateChecker(new UpdateCheckerGUI());
            UpdateCheckerGUI.Companion.getUpdateChecker().setMessage("No parameter found. Please try again with a valid parameter.");
        }
    }
}
