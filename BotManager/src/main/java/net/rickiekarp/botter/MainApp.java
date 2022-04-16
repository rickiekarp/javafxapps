package net.rickiekarp.botter;

import net.rickiekarp.botter.botservice.BotCommands;
import net.rickiekarp.botter.settings.AppConfiguration;
import net.rickiekarp.core.AppContext;
import net.rickiekarp.core.AppStarter;
import net.rickiekarp.core.account.ILoginHandler;
import net.rickiekarp.core.components.button.SidebarButton;
import net.rickiekarp.core.controller.AppLaunch;
import net.rickiekarp.core.controller.LanguageController;
import net.rickiekarp.core.debug.LogFileHandler;
import net.rickiekarp.core.settings.Configuration;
import net.rickiekarp.core.util.FileUtil;
import net.rickiekarp.core.view.MainScene;
import net.rickiekarp.core.view.MessageDialog;
import net.rickiekarp.core.view.layout.LoginMaskLayout;
import net.rickiekarp.botlib.BotConfig;
import net.rickiekarp.botlib.PluginConfig;
import net.rickiekarp.botlib.enums.BotPlatforms;
import net.rickiekarp.botlib.enums.BotType;
import net.rickiekarp.botlib.model.PluginData;
import net.rickiekarp.botter.view.BotSetupLayout;
import net.rickiekarp.botter.view.MainLayout;
import net.rickiekarp.botter.view.PluginManagerLayout;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainApp extends AppStarter implements AppLaunch, ILoginHandler {

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
        setMinHeight(550);
        setWidth(900);
        setHeight(600);
        makeResizable();
        setLayout(new MainLayout());

        super.start(stage);
        postLaunch();
    }

    @Override
    public void postLaunch() {
        AppContext.Companion.getContext().initAccountManager();

        LoginMaskLayout loginMaskLayout = new LoginMaskLayout();
        setAppContextLoginBehaviour(loginMaskLayout);
        MainScene.Companion.getMainScene().getSceneViewStack().push(loginMaskLayout.getMaskNode());

        //auto login
        if (AppContext.Companion.getContext().getAccountManager().isAutoLogin()) {
            MainScene.Companion.getMainScene().getBorderPane().setCenter(loginMaskLayout.getMaskNode());
            new Thread(loginMaskLayout.getLoginTask()).start();
        }

        try {
            new BotCommands().addBotCommands();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        //disable settings and bot setup views if no config file is present
        if (!isConfigLoaded()) {
            new MessageDialog(0, LanguageController.getString("config_not_found"), 500, 250);
            MainScene.Companion.getMainScene().getWindowScene().getWin().getSidebarButtonBox().getChildren().get(0).setDisable(true);
            MainScene.Companion.getMainScene().getWindowScene().getWin().getSidebarButtonBox().getChildren().get(1).setDisable(true);
        }
    }

    private void loadLocalPlugins() throws IOException {
        if (Configuration.Companion.getConfig().getPluginDirFile().exists()) {
            File[] pluginFileList = FileUtil.INSTANCE.getListOfFiles(Configuration.Companion.getConfig().getPluginDirFile());
            List<String> valueList; //TODO: Use HashMap instead
            for (File file : pluginFileList) {
                if (file.getName().endsWith(".jar")) {
                    try {
                        valueList = FileUtil.INSTANCE.readManifestPropertiesFromJar(file.getPath(), "Main-Class", "Version", "Type");
                        PluginData data = new PluginData(
                                valueList.get(0),
                                file.getName().substring(0, file.getName().length() - 4),
                                valueList.get(1),
                                null,
                                BotPlatforms.valueOf(valueList.get(2))
                        );

                        if (data.getPluginType() == BotPlatforms.ANDROID) {
                            loadAndroidAttributes(file, data);
                        }

                        PluginData.Companion.getPluginData().add(data);
                    } catch (IOException e) {
                        LogFileHandler.Companion.getLogger().warning("Plugin: " + file.getName() + " could not be loaded!");
                    }
                }
            }
        }
    }

    /**
     * Defines additional behaviour for the current AppContext that gets executed
     * when logging in successfully
     * @param loginMaskLayout Login Mask layout
     */
    @Override
    public void setAppContextLoginBehaviour(LoginMaskLayout loginMaskLayout) {
        loginMaskLayout.getLoginTask().setOnSucceeded(t -> {
            // This handler will be called if Task succesfully executed login code
            // disregarding result of login operation

            // and here we act according to result of login code
            if (loginMaskLayout.getLoginTask().getValue()) {
                loginMaskLayout.addAccountMenu(this);

                //set up the Client Area to display
                if (isValidConfig()) {
                    MainScene.Companion.getMainScene().getBorderPane().setTop(MainLayout.Companion.getInstance().getLaunchNode());
                    MainScene.Companion.getMainScene().getBorderPane().setCenter(null);
                } else {
                    MainScene.Companion.getMainScene().getBorderPane().setCenter(new BotSetupLayout().getLayout());
                }

                //load plugins
                try {
                    loadLocalPlugins();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //add additional buttons in the sidebar
                addSidebarButtons(MainScene.Companion.getMainScene().getBorderPane().getHeight());

            } else {
                final Task<Boolean> retryTask = loginMaskLayout.doLogin();
                loginMaskLayout.setLoginTask(retryTask);
                setAppContextLoginBehaviour(loginMaskLayout);
            }
        });
    }

    @Override
    public void setOnLogout() {
        MainLayout.Companion.getInstance().clearModules();
    }

    private void loadAndroidAttributes(File file, PluginData data) throws IOException {
        List<String> valueList = FileUtil.INSTANCE.readManifestPropertiesFromJar(file.getPath(), "Package", "Activity");
        data.setPluginPackage(valueList.get(0));
        data.setPluginActvity(valueList.get(1));
    }

    /**
     * Evaluates the current WebDriver configuration
     * @return Status of the configuration where true = good, false = needs setup
     */
    private boolean isValidConfig() {
        return BotConfig.INSTANCE.getNodeBinary() != null && PluginConfig.INSTANCE.getBrowserProfileName() != null && !(PluginConfig.INSTANCE.getBotType() == BotType.Bot.CHROME && PluginConfig.INSTANCE.getChromeConfigDirectory() == null);
    }

    /**
     * Adds additional buttons to the sidebar
     * @param stageHeight Height of the stage
     */
    private void addSidebarButtons(double stageHeight) {
        SidebarButton pluginButton = new SidebarButton("pluginmanager");
        pluginButton.setOnAction(event -> {
            new PluginManagerLayout();
            MainScene.Companion.getMainScene().getWindowScene().getWin().toggleSideBar();
        });

        MainScene.Companion.getMainScene().getWindowScene().getWin().getContentController().addSidebarItem(1, pluginButton);
        MainScene.Companion.getMainScene().getWindowScene().getWin().calcSidebarButtonSize(stageHeight);
    }
}
