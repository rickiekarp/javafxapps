package net.rickiekarp.qaacc.factory

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.qaacc.model.Account
import net.rickiekarp.qaacc.settings.AppConfiguration
import net.rickiekarp.qaacc.view.AccountEditDialog
import net.rickiekarp.qaacc.view.AccountOverview
import net.rickiekarp.qaacc.view.BugReportSettings
import net.rickiekarp.qaacc.view.MainLayout
import org.w3c.dom.*
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.util.logging.Level

object AccountXmlFactory {

    private val docFactory = DocumentBuilderFactory.newInstance()

    @Throws(TransformerConfigurationException::class, ParserConfigurationException::class, MalformedURLException::class)
    private fun createAccXML(projID: Int, name: String, mail: String, level: String, alliance: String) {

        val dataDir = Configuration.config.configDirFile
        if (!dataDir.exists()) {
            dataDir.mkdir()
        }

        LogFileHandler.logger.log(Level.INFO, "Accounts file doesn't exist. Creating...")
        val dBuilder = docFactory.newDocumentBuilder()
        val doc = dBuilder.newDocument()

        // root element
        val rootElement = doc.createElement("accountlist")
        doc.appendChild(rootElement)

        // account element
        val favAcc = doc.createElement("favAcc")
        favAcc.setAttribute(AppConfiguration.projectData[projID].getProjectAttribute(), "-1")
        rootElement.appendChild(favAcc)

        // account element
        val account = doc.createElement("account")
        account.setAttribute("id", "0")
        rootElement.appendChild(account)

        // accname element
        val accname = doc.createElement("name")
        accname.appendChild(doc.createTextNode(name))
        account.appendChild(accname)

        // accmail element
        val accmail = doc.createElement("mail")
        accmail.appendChild(doc.createTextNode(mail))
        account.appendChild(accmail)

        // acclevel element
        val acclevel = doc.createElement("level")
        acclevel.appendChild(doc.createTextNode(level))
        account.appendChild(acclevel)

        // alliance element
        val eAlliance = doc.createElement("alliance")
        eAlliance.appendChild(doc.createTextNode(alliance))
        account.appendChild(eAlliance)


        // write content into xml file
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        //transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        val source = DOMSource(doc)
        val result = StreamResult(File(dataDir.path + File.separator + AppConfiguration.projectData[projID].getProjectXML()))

        try {
            transformer.transform(source, result)
        } catch (e1: TransformerException) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

        MainLayout.status.text = LanguageController.getString("accSaved")
        LogFileHandler.logger.log(Level.INFO, "File created in " + dataDir.path.toString() + "!")
    }

    @Throws(TransformerException::class, ParserConfigurationException::class, IOException::class, SAXException::class)
    fun addAccount(projID: Int, name: String, mail: String, level: String, alliance: String) {

        if (File(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projID].getProjectXML()).exists()) {
            run {
                try {
                    val docBuilder = docFactory.newDocumentBuilder()
                    val doc = docBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projID].getProjectXML())

                    val nList = doc.getElementsByTagName("account")

                    val accounts = doc.getElementsByTagName("accountlist").item(0) // get accountlist node by tag name

                    // append a new node to accountlist
                    val account = doc.createElement("account")
                    account.setAttribute("id", nList.length.toString())
                    accounts.appendChild(account)

                    val accname = doc.createElement("name")
                    accname.appendChild(doc.createTextNode(name))
                    account.appendChild(accname)

                    val accmail = doc.createElement("mail")
                    accmail.appendChild(doc.createTextNode(mail))
                    account.appendChild(accmail)

                    val acclevel = doc.createElement("level")
                    acclevel.appendChild(doc.createTextNode(level))
                    account.appendChild(acclevel)

                    val eAlliance = doc.createElement("alliance")
                    eAlliance.appendChild(doc.createTextNode(alliance))
                    account.appendChild(eAlliance)


                    // write the content into xml file
                    val transformerFactory = TransformerFactory.newInstance()
                    val transformer = transformerFactory.newTransformer()
                    //transformer.setOutputProperty(OutputKeys.INDENT,"yes");
                    val source = DOMSource(doc)
                    val result = StreamResult(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projID].getProjectXML())
                    transformer.transform(source, result)

                    LogFileHandler.logger.log(Level.INFO, nList.length.toString() + " accounts stored in " + AppConfiguration.projectData[projID].getProjectXML() + "!")
                    MainLayout.status.setText(LanguageController.getString("accSaved"))

                } catch (e1: ParserConfigurationException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                } catch (e1: TransformerException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                } catch (e1: IOException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                } catch (e1: SAXException) {
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        ExceptionHandler(Thread.currentThread(), e1)
                    }
                }
            }
        } else {
            createAccXML(projID, name, mail, level, alliance)
        }
    }

    //loads all accounts of a project
    fun loadAccData(pjIdx: Int) {
        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[pjIdx].getProjectXML())

            doc.documentElement.normalize()

            val nList = doc.getElementsByTagName("account")

            LogFileHandler.logger.log(Level.INFO, "READING: " + AppConfiguration.projectData[pjIdx].getProjectXML())

            if (nList.length == 0) {
                LogFileHandler.logger.log(Level.INFO, "No account found in " + AppConfiguration.projectData[pjIdx].getProjectXML())
            } else {
                for (temp in 0 until nList.length) {

                    val nNode = nList.item(temp)

                    if (nNode.nodeType == Node.ELEMENT_NODE) {

                        val eElement = nNode as Element

                        AppConfiguration.accountData.add(Account(
                                eElement.getElementsByTagName("name").item(0).textContent,
                                eElement.getElementsByTagName("mail").item(0).textContent,
                                eElement.getElementsByTagName("level").item(0).textContent,
                                eElement.getElementsByTagName("alliance").item(0).textContent))
                    }
                }
                LogFileHandler.logger.log(Level.INFO, "Accounts loaded successfully!")
            }
        } catch (e1: FileNotFoundException) {
            LogFileHandler.logger.log(Level.INFO, "Accounts file could not be found!")
        } catch (e1: Exception) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

    }

    @Throws(MalformedURLException::class)
    fun readAccountNameFromXML(currentPJ: Int) {

        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[currentPJ].getProjectXML())

            doc.documentElement.normalize()

            val nList = doc.getElementsByTagName("account")

            if (nList.length == 0) {
                BugReportSettings.accountCB.isDisable = true
                BugReportSettings.accountCB.setValue(LanguageController.getString("no_account_found"))
            } else {
                for (temp in 0 until nList.length) {

                    val nNode = nList.item(temp)

                    if (nNode.nodeType == Node.ELEMENT_NODE) {

                        val eElement = nNode as Element

                        AppConfiguration.accountList.addAll(eElement.getElementsByTagName("name").item(0).textContent.toString())
                    }
                }
                BugReportSettings.accountCB.items = AppConfiguration.accountList
                if (AppConfiguration.projectData[currentPJ].getProjectAccBookmarkIdx() >= 0) {
                    BugReportSettings.accountCB.value = AppConfiguration.accountList[AppConfiguration.projectData[currentPJ].getProjectAccBookmarkIdx()]
                }
            }
        } catch (e: FileNotFoundException) {
            BugReportSettings.accountCB.isDisable = true
            BugReportSettings.accountCB.value = LanguageController.getString("xml_not_found")
        } catch (e1: Exception) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

    }

    //returns the bookmarked account ID of a project
    @Throws(TransformerException::class, ParserConfigurationException::class, IOException::class, SAXException::class)
    internal fun getFavAccID(projectID: Int): Int {
        if (File(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projectID].getProjectXML()).exists()) {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projectID].getProjectXML())
            return Integer.parseInt(doc.getElementsByTagName("favAcc").item(0).attributes.getNamedItem(AppConfiguration.projectData[projectID].getProjectAttribute()).nodeValue)
        } else {
            return -1
        }
    }

    //returns the bookmarked account name of a project
    @Throws(MalformedURLException::class)
    fun getFavAccName(projectID: Int): String {
        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projectID].getProjectXML())
            doc.documentElement.normalize()

            val accNode = doc.getElementsByTagName("account").item(AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx()).firstChild
            return accNode.textContent
        } catch (e1: ArrayIndexOutOfBoundsException) {
            //ignore
        } catch (e1: FileNotFoundException) {
        } catch (e1: NullPointerException) {
            LogFileHandler.logger.log(Level.WARNING, "account index error!")
            AppConfiguration.projectData[projectID].setProjectAccBookmarkIdx(-1)
        } catch (e1: Exception) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

        return ""
    }

    //removes the account of the given project & index
    @Throws(MalformedURLException::class)
    fun removeAccountFromXml(projectID: Int, selectedIndex: Int) {
        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projectID].getProjectXML())

            doc.documentElement.normalize()

            val nList = doc.getElementsByTagName("account")
            val element = doc.getElementsByTagName("account").item(selectedIndex) as Element

            //compares selected account with saved account and updates the variable accordingly
            if (Integer.parseInt(element.getAttribute("id")) < AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx()) {
                LogFileHandler.logger.config("change_favAcc: " + AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx() + " -> " + (AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx() - 1))
                AppConfiguration.projectData[projectID].setProjectAccBookmarkIdx(projectID - 1)
            } else if (Integer.parseInt(element.getAttribute("id")) == AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx()) {
                LogFileHandler.logger.config("change_favAcc: " + AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx() + " -> " + -1)
                LogFileHandler.logger.config("change_selectedAcc: " + AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx() + " -> NONE")
                AppConfiguration.projectData[projectID].setProjectAccBookmarkIdx(-1)
                AppConfiguration.projectData[projectID].setProjectAccBookmarkName("")
            }

            doc.getElementsByTagName("favAcc").item(0).attributes.getNamedItem(AppConfiguration.projectData[projectID].getProjectAttribute()).nodeValue = AppConfiguration.projectData[projectID].getProjectAccBookmarkIdx().toString()

            //removes the element
            element.parentNode.removeChild(element)
            LogFileHandler.logger.log(Level.INFO, "Account deleted {" + element.getAttribute("id") + ", " + element.firstChild.textContent + "}")

            //iterate through nodeList and update the attributes
            for (e in 0 until nList.length) {

                // Get the account element by tag name directly
                val nodes_new = doc.getElementsByTagName("account").item(e)

                // update account attribute
                val attr = nodes_new.attributes
                val nodeAttr = attr.getNamedItem("id")
                nodeAttr.textContent = e.toString()
            }

            // write content into xml file
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            //transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            val source = DOMSource(doc)
            val result = StreamResult(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projectID].getProjectXML())
            try {
                transformer.transform(source, result)
                AccountOverview.status.style = "-fx-text-fill: red;"
                AccountOverview.status.text = LanguageController.getString("accDeleted")
            } catch (e1: TransformerException) {
                if (DebugHelper.DEBUGVERSION) {
                    e1.printStackTrace()
                } else {
                    ExceptionHandler(Thread.currentThread(), e1)
                }
            }

            AccountOverview.nameTF.text = ""
            AccountOverview.mailTF.text = ""
            AccountOverview.lvTF.text = ""
            AccountOverview.alliTF.text = ""
            if (nList.length == 0) {
                AccountOverview.editAcc.isDisable = true
                AccountOverview.delAcc.isDisable = true
            }
            if (nList.length == 1) {
                AccountOverview.accCount.text = "1 " + LanguageController.getString("acc_loaded")
            } else {
                AccountOverview.accCount.text = nList.length.toString() + " " + LanguageController.getString("accs_loaded")
            }
        } catch (e1: Exception) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

    }

    @Throws(MalformedURLException::class)
    fun saveAccXml(projID: Int, i: Int) {
        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projID].getProjectXML())
            doc.documentElement.normalize()

            val eName = doc.getElementsByTagName("account").item(i).childNodes.item(0) as Element
            val eMail = doc.getElementsByTagName("account").item(i).childNodes.item(1) as Element
            val eLevel = doc.getElementsByTagName("account").item(i).childNodes.item(2) as Element
            val eAlliance = doc.getElementsByTagName("account").item(i).childNodes.item(3) as Element
            eName.textContent = AccountEditDialog.accNameTF.text
            eMail.textContent = AccountEditDialog.accMailTF.text
            eLevel.textContent = AccountEditDialog.accLevelTF.text
            eAlliance.textContent = AccountEditDialog.accAllianceTF.text

            // write content into xml file
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            //transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            val source = DOMSource(doc)
            val result = StreamResult(Configuration.config.configDirFile.toString() + File.separator + AppConfiguration.projectData[projID].getProjectXML())
            try {
                transformer.transform(source, result)
                AccountOverview.status.style = "-fx-text-fill: #55c4fe;"
                AccountOverview.status.text = LanguageController.getString("accSaved")
                LogFileHandler.logger.log(
                        Level.INFO,
                        "{" + i + ","
                                + eName.textContent + ","
                                + eMail.textContent + ","
                                + eLevel.textContent + ","
                                + eAlliance.textContent
                                + "} - success!"
                )
            } catch (e1: TransformerException) {
                if (DebugHelper.DEBUGVERSION) {
                    e1.printStackTrace()
                } else {
                    ExceptionHandler(Thread.currentThread(), e1)
                }
            }

        } catch (e1: Exception) {
            AccountOverview.accCount.text = "ERROR"
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

    }
}