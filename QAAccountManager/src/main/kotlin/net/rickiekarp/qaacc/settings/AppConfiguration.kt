package net.rickiekarp.qaacc.settings

import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.settings.LoadSave
import net.rickiekarp.core.util.CommonUtil
import net.rickiekarp.qaacc.model.Account
import net.rickiekarp.qaacc.model.Projects
import javafx.collections.FXCollections
import org.w3c.dom.Node

object AppConfiguration {

    /** settings variables  */
    @LoadSave
    var pjCfgSelection: Int = 0
    @LoadSave
    var acronym: String? = null
    @LoadSave
    var browser: String? = null
    @LoadSave
    var srvSel = 0
    @LoadSave
    var locaSel = 0

    /** account data  */
    var accountData = FXCollections.observableArrayList<Account>()

    /** project data  */
    var projects = arrayOf("A", "B", "C", "D")
    var projectData = FXCollections.observableArrayList<Projects>()
    var pjState: Int = 0

    /** all account name scheme details are listed here  */
    @LoadSave
    var dept: String? = null
    @LoadSave
    var mail_pref: String? = null
    @LoadSave
    var mail_end: String? = null
    @LoadSave
    var pass: String? = null

    /** bug template settings  */
    var found_in_version = ""
    var server = FXCollections.observableArrayList("QA-Test", "DEV-Test")
    var locale = FXCollections.observableArrayList("English", "Deutsch")
    var loca = FXCollections.observableArrayList("English", "Deutsch")
    //TODO: Remove account list and use accountData instead
    var accountList = FXCollections.observableArrayList<String>()

    /** bug template  */
    var bugtemplate = arrayOf("Browser & -Version: ", "Found in Version: ", "Occured on: ", "Occured at: ", "Account: ", "Language: ", "Reproducibility: ", "Repro:", "- ", "Currently:", "Expected:", " or any")

    /** sets string to clipboard  */
    fun setStringToClipboard(stringContent: String) {
        LogFileHandler.logger.warning("stub: setStringToClipboard()")
        //        StringSelection stringSelection = new StringSelection(stringContent);
        //        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    fun readProjectData() {
        val projList = Configuration.config.settingsXmlFactory!!.getNodeList("project")

        if (projList!!.length == 0) {
            for (i in projects.indices) {
                //                proj[i] = doc.createElement("project");
                //                proj[i].appendChild(doc.createTextNode(projects[i]));
                //                projects.appendChild(proj[i]);
                projectData.add(Projects(i, projects[i], "accounts_" + projects[i].toLowerCase() + ".xml", projects[i].toLowerCase() + "Idx", -1, ""))
            }
        }
        val projects = arrayOfNulls<String>(projList.length)

        for (i in 0 until projList.length) {
            val nNode = projList.item(i)
            if (nNode.nodeType == Node.ELEMENT_NODE) {
                projects[i] = nNode.textContent
                projectData.add(Projects(i, projects[i]!!, "accounts_" + projects[i]!!.toLowerCase() + ".xml", projects[i]!!.toLowerCase() + "Idx", -1, ""))
            }
        }
    }

    /**
     * returns bug template
     * only called in menu bar
     */
    fun onCopyBugtemplate(projectInt: Int): String {
        val accName: String
        val orAny: String
        if (projectData[projectInt].getProjectAccBookmarkName() == "") {
            accName = ""
            orAny = ""
        } else {
            accName = projectData[projectInt].getProjectAccBookmarkName()
            orAny = bugtemplate[11]
        }

        return bugtemplate[0] + browser + "\n" +
                bugtemplate[1] + found_in_version + "\n" +
                bugtemplate[2] + server[srvSel] + "\n" +
                bugtemplate[3] + CommonUtil.getTime("HH:mm") + "\n" +
                bugtemplate[4] + accName + orAny + "\n" +
                bugtemplate[5] + loca[locaSel] + bugtemplate[11] + "\n" +
                bugtemplate[6] + "\n\n" +
                bugtemplate[7] + "\n" +
                bugtemplate[8] + "\n\n" +
                bugtemplate[9] + "\n" +
                bugtemplate[8] + "\n\n" +
                bugtemplate[10] + "\n" +
                bugtemplate[8]
    }
}