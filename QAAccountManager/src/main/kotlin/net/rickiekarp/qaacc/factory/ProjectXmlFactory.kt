package net.rickiekarp.qaacc.factory

import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.qaacc.model.Projects
import net.rickiekarp.qaacc.settings.AppConfiguration
import net.rickiekarp.qaacc.view.BugReportSettings
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.util.logging.Level

object ProjectXmlFactory {

    private val docFactory = DocumentBuilderFactory.newInstance()

    @Throws(MalformedURLException::class, URISyntaxException::class)
    fun getSettings() {

        if (File(Configuration.config.configDirFile.toString() + File.separator + File.separator + "projects.xml").exists()) {
            readSettingsXML(File(Configuration.config.configDirFile.toString() + File.separator + "data"), false)
        } else {
            createConfigXML(Configuration.config.configDirFile)
        }
    }

    @Throws(MalformedURLException::class)
    fun createConfigXML(datafile: File) {
        if (!datafile.exists()) {
            datafile.mkdir()
        }

        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.newDocument()

            // root element
            val rootElement = doc.createElement("settings")
            doc.appendChild(rootElement)

            // projects element
            val projects = doc.createElement("proj")
            projects.setAttribute("selected", "-1")
            rootElement.appendChild(projects)

            // projects element
            val proj = arrayOfNulls<Element>(AppConfiguration.projects.size)
            for (i in 0 until AppConfiguration.projects.size) {
                proj[i] = doc.createElement("project")
                proj[i]!!.appendChild(doc.createTextNode(AppConfiguration.projects[i]))
                projects.appendChild(proj[i])
                AppConfiguration.projectData.add(Projects(i, AppConfiguration.projects[i], "accounts_" + AppConfiguration.projects[i].toLowerCase() + ".xml", AppConfiguration.projects[i].toLowerCase() + "Idx", -1, ""))
            }

            // acronym element
            val acronym = doc.createElement("name")
            acronym.appendChild(doc.createTextNode(""))
            rootElement.appendChild(acronym)

            // browser element
            val browser = doc.createElement("browser")
            browser.appendChild(doc.createTextNode(""))
            rootElement.appendChild(browser)

            // server element
            val server = doc.createElement("srv")
            server.appendChild(doc.createTextNode("0"))
            rootElement.appendChild(server)

            // language element
            val language = doc.createElement("loca")
            language.appendChild(doc.createTextNode("0"))
            rootElement.appendChild(language)

            // name scheme element
            val nScheme = doc.createElement("namescheme")
            rootElement.appendChild(nScheme)

            // name scheme element
            val nScheme1 = doc.createElement("dept")
            nScheme1.appendChild(doc.createTextNode("qa"))
            nScheme.appendChild(nScheme1)

            // name scheme element
            val nScheme2 = doc.createElement("mail_pref")
            nScheme2.appendChild(doc.createTextNode("mailcheck+"))
            nScheme.appendChild(nScheme2)

            // name scheme element
            val nScheme3 = doc.createElement("mail_end")
            nScheme3.appendChild(doc.createTextNode("@placeholder.com"))
            nScheme.appendChild(nScheme3)

            // name scheme element
            val nScheme4 = doc.createElement("pass")
            nScheme4.appendChild(doc.createTextNode("test1"))
            nScheme.appendChild(nScheme4)


            // write content into xml file
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            //                transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
            val source = DOMSource(doc)
            val result = StreamResult(datafile.path + File.separator + "project.xml")

            try {
                transformer.transform(source, result)
            } catch (e1: TransformerException) {
                if (DebugHelper.DEBUGVERSION) {
                    e1.printStackTrace()
                } else {
                    ExceptionHandler(Thread.currentThread(), e1)
                }
            }

            readSettingsXML(datafile, true)

        } catch (e1: Exception) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }

    }

    @Throws(MalformedURLException::class)
    fun readSettingsXML(dataDir: File, wasJustCreated: Boolean) {

        try {
            val dBuilder = docFactory.newDocumentBuilder()
            val doc = dBuilder.parse(File(dataDir.toString() + File.separator + "project.xml"))

            doc.documentElement.normalize()

            //reads the projects from the xml and adds them to a list
            if (!wasJustCreated) {
                val projList = doc.getElementsByTagName("project")

                val projects = arrayOfNulls<String>(projList.length)

                for (i in 0 until projList.length) {
                    val nNode = projList.item(i)
                    if (nNode.nodeType == Node.ELEMENT_NODE) {
                        projects[i] = nNode.textContent
                        AppConfiguration.projectData.add(Projects(i, projects[i]!!, "accounts_" + projects[i]!!.toLowerCase() + ".xml", projects[i]!!.toLowerCase() + "Idx", -1, ""))
                    }
                }
            }

            //selected project
            val proj = Integer.parseInt(doc.getElementsByTagName("proj").item(0).attributes.getNamedItem("selected").nodeValue)
            if (AppConfiguration.projectData.size <= proj) {
                AppConfiguration.pjCfgSelection = -1
            } else {
                AppConfiguration.pjCfgSelection = proj
            }

            //name scheme
            val namescheme_dept = doc.getElementsByTagName("dept").item(0).textContent
            if (namescheme_dept == null) {
                AppConfiguration.dept = ""
            } else {
                AppConfiguration.dept = namescheme_dept
            }

            val mail_pref = doc.getElementsByTagName("mail_pref").item(0).textContent
            if (mail_pref == null) {
                AppConfiguration.mail_pref = ""
            } else {
                AppConfiguration.mail_pref = mail_pref
            }

            val mail_end = doc.getElementsByTagName("mail_end").item(0).textContent
            if (mail_end == null) {
                AppConfiguration.mail_end = ""
            } else {
                AppConfiguration.mail_end = mail_end
            }

            val pass = doc.getElementsByTagName("pass").item(0).textContent
            if (pass == null) {
                AppConfiguration.pass = ""
            } else {
                AppConfiguration.pass = pass
            }

            //user initials
            val acr = doc.getElementsByTagName("name").item(0).textContent
            if (acr == null) {
                AppConfiguration.acronym = ""
            } else {
                AppConfiguration.acronym = acr
            }

            //browser
            val browser = doc.getElementsByTagName("browser").item(0).textContent
            if (browser == null) {
                AppConfiguration.browser = ""
            } else {
                AppConfiguration.browser = browser
            }

            //server
            val server = Integer.parseInt(doc.getElementsByTagName("srv").item(0).textContent)
            when (server) {
                0 -> AppConfiguration.srvSel = 0
                1 -> AppConfiguration.srvSel = 1
                else -> AppConfiguration.srvSel = 0
            }

            //localization language
            val templ_language = Integer.parseInt(doc.getElementsByTagName("loca").item(0).textContent)
            when (templ_language) {
                0 -> AppConfiguration.locaSel = 0
                1 -> AppConfiguration.locaSel = 1
                else -> AppConfiguration.locaSel = 0
            }

            for (i in 0 until AppConfiguration.projectData.size) {
                AppConfiguration.projectData[i].setProjectAccBookmarkIdx(AccountXmlFactory.getFavAccID(i))
                AppConfiguration.projectData[i].setProjectAccBookmarkName(AccountXmlFactory.getFavAccName(i))
            }


        } catch (e1: Exception) {
            //            if (Constants.debugVersion) { e1.printStackTrace(); } else { new ExceptionHandler(Thread.currentThread(), e1); }
        }

    }

    @Throws(MalformedURLException::class, FileNotFoundException::class)
    fun saveSettings() {

        val cfgXml = File(Configuration.config.jarFile.parent + File.separator + "data")

        if (File(cfgXml.path + File.separator + "project.xml").exists()) {

            try {
                val dBuilder = docFactory.newDocumentBuilder()
                val doc = dBuilder.parse(File(cfgXml.toString() + File.separator + "project.xml"))

                doc.documentElement.normalize()

                doc.getElementsByTagName("proj").item(0).attributes.getNamedItem("selected").nodeValue = AppConfiguration.pjCfgSelection.toString()

                if (Configuration.debugState) {
                    doc.getElementsByTagName("dbg").item(0).textContent = "1"
                } else {
                    doc.getElementsByTagName("dbg").item(0).textContent = "0"
                }

                if (Configuration.logState) {
                    doc.getElementsByTagName("logs").item(0).textContent = "1"
                } else {
                    doc.getElementsByTagName("logs").item(0).textContent = "0"
                }

                doc.getElementsByTagName("theme").item(0).textContent = Configuration.themeState.toString()

                doc.getElementsByTagName("colorscheme").item(0).textContent = Configuration.colorScheme.toString()

                if (Configuration.animations) {
                    doc.getElementsByTagName("anim").item(0).textContent = "1"
                } else {
                    doc.getElementsByTagName("anim").item(0).textContent = "0"
                }

                doc.getElementsByTagName("name").item(0).textContent = AppConfiguration.acronym
                doc.getElementsByTagName("browser").item(0).textContent = AppConfiguration.browser
                doc.getElementsByTagName("srv").item(0).textContent = AppConfiguration.srvSel.toString()
                doc.getElementsByTagName("loca").item(0).textContent = AppConfiguration.locaSel.toString()

                doc.getElementsByTagName("locale").item(0).attributes.getNamedItem("ID").nodeValue = Configuration.language.toString()

                // write the content into xml file
                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                val source = DOMSource(doc)
                val result = StreamResult(File(cfgXml, "project.xml"))
                transformer.transform(source, result)
                LogFileHandler.logger.log(Level.INFO, "config.save:Success")
            } catch (e1: Exception) {
                if (DebugHelper.DEBUGVERSION) {
                    e1.printStackTrace()
                } else {
                    ExceptionHandler(Thread.currentThread(), e1)
                }
            }

        } else {
            createConfigXML(cfgXml.parentFile)
            saveSettings()
        }
    }

    /** adds a new project  */
    @Throws(MalformedURLException::class, FileNotFoundException::class)
    fun addProject(projectName: String) {

        if (File(Configuration.config.jarFile.path + File.separator + "project.xml").exists()) {


            try {
                val dBuilder = docFactory.newDocumentBuilder()
                val doc = dBuilder.parse(Configuration.config.jarFile.path + File.separator + "project.xml")
                doc.documentElement.normalize()

                val nList = doc.getElementsByTagName("project")
                val projects = doc.getElementsByTagName("proj").item(0)

                // project element
                val proj = doc.createElement("project")
                proj.appendChild(doc.createTextNode(projectName))
                projects.appendChild(proj)

                // write the content into xml file
                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                //                transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
                val source = DOMSource(doc)
                val result = StreamResult(Configuration.config.jarFile.path + File.separator + "project.xml")
                transformer.transform(source, result)

                //adds new project to projectData list
                AppConfiguration.projectData.add(Projects(nList.length + 1, projectName, "accounts_" + projectName.toLowerCase() + ".xml", projectName.toLowerCase() + "Idx", -1, ""))

            } catch (e1: ParserConfigurationException) {
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
            } catch (e1: IOException) {
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
            }

        } else {
            createConfigXML(Configuration.config.jarFile.parentFile)
            addProject(projectName)
        }
    }

    @Throws(MalformedURLException::class, FileNotFoundException::class)
    fun saveBugTemplateSettings(pjState: Int) {

        val cfgXml = File(Configuration.config.jarFile.parent + File.separator + "data")

        if (File(cfgXml.path + File.separator + "project.xml").exists()) {
            try {
                val dBuilder = docFactory.newDocumentBuilder()
                val cfgdoc = dBuilder.parse(File(cfgXml.toString() + File.separator + "project.xml"))

                cfgdoc.documentElement.normalize()

                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()

                cfgdoc.getElementsByTagName("browser").item(0).textContent = BugReportSettings.browserTF.text
                cfgdoc.getElementsByTagName("srv").item(0).textContent = BugReportSettings.serverCB.selectionModel.selectedIndex.toString()
                cfgdoc.getElementsByTagName("loca").item(0).textContent = BugReportSettings.locaCB.selectionModel.selectedIndex.toString()


                // write the content into xml file
                val cfgSrc = DOMSource(cfgdoc)
                val result = StreamResult(File(cfgXml, "project.xml"))
                transformer.transform(cfgSrc, result)

                if (pjState >= 0) {
                    if (File(Configuration.config.jarFile.parent + File.separator + "data" + File.separator + AppConfiguration.projectData[pjState].getProjectXML()).exists()) {

                        val accDoc = dBuilder.parse(Configuration.config.jarFile.parent + File.separator + "data" + File.separator + AppConfiguration.projectData[pjState].getProjectXML())
                        accDoc.documentElement.normalize()

                        accDoc.getElementsByTagName("favAcc").item(0).attributes.getNamedItem(AppConfiguration.projectData[pjState].getProjectAttribute()).nodeValue = BugReportSettings.accountCB.selectionModel.selectedIndex.toString()

                        val accSrc = DOMSource(accDoc)
                        val result2 = StreamResult(Configuration.config.jarFile.parent + File.separator + "data" + File.separator + AppConfiguration.projectData[pjState].getProjectXML())
                        transformer.transform(accSrc, result2)
                    }
                }


                LogFileHandler.logger.log(Level.INFO, "config.save:Success")
            } catch (e1: Exception) {
                if (DebugHelper.DEBUGVERSION) {
                    e1.printStackTrace()
                } else {
                    ExceptionHandler(Thread.currentThread(), e1)
                }
            }

        } else {
            createConfigXML(Configuration.config.jarFile.parentFile)
            saveBugTemplateSettings(pjState)
        }

    }
}