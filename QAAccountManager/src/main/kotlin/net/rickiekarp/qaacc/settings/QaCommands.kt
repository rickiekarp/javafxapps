package net.rickiekarp.qaacc.settings

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.model.ConsoleCommands
import net.rickiekarp.core.settings.AppCommands

/**
 * This class is used to define additional application related console commands.
 * Simply add the new command to the commands list and define the method to execute.
 */
class QaCommands {
    @Throws(NoSuchMethodException::class)
    fun addCommands() {
        AppCommands.commandsList.addAll(
                ConsoleCommands("/addProject", "NAME", LanguageController.getString("addProject_desc")) {
                    addProject("dummyProject")
                },
                ConsoleCommands("/botAccount", "PROJECT NAME AMOUNT", LanguageController.getString("addAccounts_desc")) {
                    addAccounts("dummyAccount")
                }
        )
    }

    fun addProject(str: String) {
        println(str)
        //            else if (command.equals(commandsList.get(6).getCommandName() + " " + str[2])) {
        //            try {
        //                SettingsXmlFactory.addProject(str[2]);
        //                MainScene.gameComboBox.getItems().add(str[2]);
        //                MainScene.consoleStatus.setText(LanguageController.getString("addProject_success"));
        //            } catch (MalformedURLException | FileNotFoundException e1) {
        //                if (AppConfiguration.debugVersion) { e1.printStackTrace(); } else { new ExceptionHandler(Thread.currentThread(), e1); }
        //            }
        //        }
    }

    fun addAccounts(str: String) {
        println(str)

        //            else if (command.equals(commandsList.get(7).getCommandName() + " " + str[2] + " " + str[3] + " " + str[4])) {
        //            try {
        //                int amount = Integer.parseInt(str[4]);
        //
        //                if (amount >= 1) {
        //                    for (int i = 0; i < amount; i++) {
        //                        AccountXmlFactory.addAccount(Integer.parseInt(str[2]), str[3] + i, AppConfiguration.mail_pref + str[3] + i + AppConfiguration.mail_end, "1", "");
        //                    }
        //                    if (amount == 1) {
        //                        MainScene.consoleStatus.setText(LanguageController.getString("accAdded"));
        //                    } else {
        //                        MainScene.consoleStatus.setText(LanguageController.getString("accsAdded"));
        //                    }
        //                }
        //            } catch (ParserConfigurationException | IOException | TransformerException | SAXException e1) {
        //                if (AppConfiguration.debugVersion) { e1.printStackTrace(); } else { new ExceptionHandler(Thread.currentThread(), e1); }
        //            } catch (NumberFormatException | IndexOutOfBoundsException e1) {
        //                //ignore
        //            }
    }
}


