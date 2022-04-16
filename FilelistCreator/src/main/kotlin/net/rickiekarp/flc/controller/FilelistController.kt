package net.rickiekarp.flc.controller

import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.util.CommonUtil
import net.rickiekarp.flc.settings.AppConfiguration
import net.rickiekarp.flc.view.layout.MainLayout

import java.io.File
import java.io.FileWriter
import java.io.IOException

class FilelistController {
    private val lenght = IntArray(MainLayout.fileColumn.size)
    private var dirlenght: Int = 0

    val list: String
        get() {
            val spacing = 4
            val diffColumn = IntArray(MainLayout.fileColumn.size)
            val separatorLine: String

            val sb = StringBuilder()

            if (canShowHeader) {
                sb.append("FileList\n")
                sb.append("-----------------------------------------------\n")
                sb.append(LanguageController.getString("fcreation")).append(": ").append(CommonUtil.getDate("dd.MM.yyyy")).append(" ").append(LanguageController.getString("at")).append(" ").append(CommonUtil.getTime("HH:mm")).append("\n")
                sb.append(LanguageController.getString("fpath")).append(": ").append(MainLayout.pathTF.text).append("\n")
                sb.append("-----------------------------------------------\n\n")
            }

            //calculate column space
            for (i in MainLayout.fileColumn.indices) {
                if (option[i]) {
                    sb.append(MainLayout.fileColumn[i]!!.text)
                    when (sortingIdx) {
                        //sorting is: none
                        0 -> {
                            diffColumn[i] = lenght[i] - MainLayout.fileColumn[i]!!.text.length + spacing
                            // check if column space difference is smaller than 3
                            // if this is the case, set a minimum space of 2
                            if (diffColumn[i] < 3) {
                                diffColumn[i] = 2
                            }
                        }
                        //sorting is: folder
                        1 -> {
                            //check if filename is longer than directory name
                            if (i == 0) {
                                if (lenght[i] > dirlenght) {
                                    diffColumn[i] = lenght[i] - MainLayout.fileColumn[i]!!.text.length + spacing
                                } else {
                                    diffColumn[i] = dirlenght - MainLayout.fileColumn[i]!!.text.length + spacing
                                }
                            } else {
                                diffColumn[i] = lenght[i] - MainLayout.fileColumn[i]!!.text.length + spacing
                            }
                            if (diffColumn[i] < 3) {
                                diffColumn[i] = 2
                            }
                        }
                    }
                    sb.append(" ".repeat(Math.max(0, diffColumn[i])))
                }
            }

            //contruct separator line string
            separatorLine = getSeparatorLine(diffColumn)
            sb.append("\n")

            when (sortingIdx) {
                //sorting is 'none'
                0 -> {
                    sb.append(separatorLine)
                    sb.append("\n")

                    for (pos in 0 until AppConfiguration.fileData.size) {
                        if (option[0]) {
                            sb.append(AppConfiguration.fileData[pos].getFilename())
                            val diff = lenght[0] - AppConfiguration.fileData[pos].getFilename().length + MainLayout.fileColumn[0]!!.text.length - lenght[0] + diffColumn[0]
                            sb.append(" ".repeat(Math.max(0, diff)))
                        }

                        if (option[1]) {
                            sb.append(AppConfiguration.fileData[pos].getFiletype())
                            val diff1 = lenght[1] - AppConfiguration.fileData[pos].getFiletype().length + MainLayout.fileColumn[1]!!.text.length - lenght[1] + diffColumn[1]
                            sb.append(" ".repeat(Math.max(0, diff1)))
                        }

                        if (option[2]) {
                            sb.append(AppConfiguration.fileData[pos].getFilepath())
                            val diff2 = lenght[2] - AppConfiguration.fileData[pos].getFilepath().length + MainLayout.fileColumn[2]!!.text.length - lenght[2] + diffColumn[2]
                            sb.append(" ".repeat(Math.max(0, diff2)))
                        }

                        if (option[3]) {
                            sb.append(MainLayout.fileTable.columns[3].getCellData(pos))
                            val diff3 = lenght[3] - MainLayout.fileTable.columns[3].getCellData(pos).toString().length + MainLayout.fileColumn[3]!!.text.length - lenght[3] + diffColumn[3]
                            sb.append(" ".repeat(Math.max(0, diff3)))
                        }

                        if (option[4]) {
                            sb.append(AppConfiguration.fileData[pos].getCreationDate())
                            val diff4 = lenght[4] - AppConfiguration.fileData[pos].getCreationDate().length + MainLayout.fileColumn[4]!!.text.length - lenght[4] + diffColumn[4]
                            sb.append(" ".repeat(Math.max(0, diff4)))
                        }

                        if (option[5]) {
                            sb.append(AppConfiguration.fileData[pos].lastModif)
                            val diff5 = lenght[5] - AppConfiguration.fileData[pos].lastModif.length + MainLayout.fileColumn[5]!!.text.length - lenght[5] + diffColumn[5]
                            sb.append(" ".repeat(Math.max(0, diff5)))
                        }

                        if (option[6]) {
                            sb.append(AppConfiguration.fileData[pos].getLastAccessDate())
                            val diff6 = lenght[6] - AppConfiguration.fileData[pos].getLastAccessDate().length + MainLayout.fileColumn[6]!!.text.length - lenght[6] + diffColumn[6]
                            sb.append(" ".repeat(Math.max(0, diff6)))
                        }

                        if (option[7]) {
                            sb.append(AppConfiguration.fileData[pos].getIsHidden())
                            val diff7 = lenght[7] - AppConfiguration.fileData[pos].getIsHidden().length + MainLayout.fileColumn[7]!!.text.length - lenght[7] + diffColumn[7]
                            sb.append(" ".repeat(Math.max(0, diff7)))
                        }

                        sb.append("\n")
                    }

                    sb.append(separatorLine)
                    sb.append("\n")

                    if (AppConfiguration.subFolderCheck) {
                        var totalSize: Long = 0
                        for (i in 0 until AppConfiguration.dirData.size) {
                            totalSize += AppConfiguration.dirData[i].fileSizeInDir
                        }
                        sb.append(AppConfiguration.fileData.size)
                                .append(" ").append(LanguageController.getString("files")).append("; ")
                                .append(totalSize).append(" Bytes")
                    } else {
                        sb.append(AppConfiguration.dirData[0].filesInDir)
                                .append(" ").append(LanguageController.getString("files")).append("; ")
                                .append(AppConfiguration.dirData[0].fileSizeInDir).append(" Bytes")
                    }
                }
                1 -> {
                    sb.append(separatorLine)
                    sb.append("\n")

                    for (pos1 in 0 until AppConfiguration.dirData.size) {

                        if (AppConfiguration.dirData[pos1].filesInDir > 0 || canShowEmptyFolder) {
                            //add path
                            sb.append(AppConfiguration.dirData[pos1].getDir()).append("\n")
                            sb.append(separatorLine)
                            sb.append("\n")

                            //add files of subdir
                            for (pos in 0 until AppConfiguration.fileData.size) {
                                if (AppConfiguration.fileData[pos].getFilepath() == AppConfiguration.dirData[pos1].getDir()) {
                                    if (option[0]) {
                                        sb.append(AppConfiguration.fileData[pos].getFilename())
                                        val diff = lenght[0] - AppConfiguration.fileData[pos].getFilename().length + MainLayout.fileColumn[0]!!.text.length - lenght[0] + diffColumn[0]
                                        sb.append(" ".repeat(Math.max(0, diff)))
                                    }

                                    if (option[1]) {
                                        sb.append(AppConfiguration.fileData[pos].getFiletype())
                                        val diff1 = lenght[1] - AppConfiguration.fileData[pos].getFiletype().length + MainLayout.fileColumn[1]!!.text.length - lenght[1] + diffColumn[1]
                                        sb.append(" ".repeat(Math.max(0, diff1)))
                                    }

                                    if (option[2]) {
                                        sb.append(AppConfiguration.fileData[pos].getFilepath())
                                        val diff2 = lenght[2] - AppConfiguration.fileData[pos].getFilepath().length + MainLayout.fileColumn[2]!!.text.length - lenght[2] + diffColumn[2]
                                        sb.append(" ".repeat(Math.max(0, diff2)))
                                    }

                                    if (option[3]) {
                                        sb.append(MainLayout.fileTable.columns[3].getCellData(pos))
                                        val diff3 = lenght[3] - MainLayout.fileTable.columns[3].getCellData(pos).toString().length + MainLayout.fileColumn[3]!!.text.length - lenght[3] + diffColumn[3]
                                        sb.append(" ".repeat(Math.max(0, diff3)))
                                    }

                                    if (option[4]) {
                                        sb.append(AppConfiguration.fileData[pos].getCreationDate())
                                        val diff4 = lenght[4] - AppConfiguration.fileData[pos].getCreationDate().length + MainLayout.fileColumn[4]!!.text.length - lenght[4] + diffColumn[4]
                                        sb.append(" ".repeat(Math.max(0, diff4)))
                                    }

                                    if (option[5]) {
                                        sb.append(AppConfiguration.fileData[pos].lastModif)
                                        val diff5 = lenght[5] - AppConfiguration.fileData[pos].lastModif.length + MainLayout.fileColumn[5]!!.text.length - lenght[5] + diffColumn[5]
                                        sb.append(" ".repeat(Math.max(0, diff5)))
                                    }

                                    if (option[6]) {
                                        sb.append(AppConfiguration.fileData[pos].getLastAccessDate())
                                        val diff6 = lenght[6] - AppConfiguration.fileData[pos].getLastAccessDate().length + MainLayout.fileColumn[6]!!.text.length - lenght[6] + diffColumn[6]
                                        sb.append(" ".repeat(Math.max(0, diff6)))
                                    }

                                    if (option[7]) {
                                        sb.append(AppConfiguration.fileData[pos].getIsHidden())
                                        val diff7 = lenght[7] - AppConfiguration.fileData[pos].getIsHidden().length + MainLayout.fileColumn[7]!!.text.length - lenght[7] + diffColumn[7]
                                        sb.append(" ".repeat(Math.max(0, diff7)))
                                    }

                                    sb.append("\n")
                                }
                            }
                            sb.append(separatorLine)
                            sb.append("\n")
                            sb.append(AppConfiguration.dirData[pos1].filesInDir)
                                    .append(" ").append(LanguageController.getString("files")).append("; ")
                                    .append(AppConfiguration.dirData[pos1].fileSizeInDir).append(" Bytes")

                            sb.append("\n\n")
                        }
                    }

                    sb.append(separatorLine)
                    sb.append("\n")
                    var totalSize: Long = 0
                    for (i in 0 until AppConfiguration.dirData.size) {
                        totalSize += AppConfiguration.dirData[i].fileSizeInDir
                    }
                    sb.append(AppConfiguration.fileData.size)
                            .append(" ").append(LanguageController.getString("files")).append("; ")
                            .append(totalSize).append(" Bytes")
                }
            }
            return sb.toString()
        }

    /**
     * Builds the FileList in an HTML format and returns the HTML code as a string
     */
    private val html: String
        get() {
            val html = StringBuilder()
            html.append("<table border=\"1\" style=\"width:100%\">\n" + "<h1>File List</h1>\n" + "<h2>Created: ").append(CommonUtil.getDate("dd.MM.yyyy")).append(" at ").append(CommonUtil.getTime("HH:mm")).append("</h2>\n")
            var i = 0
            while (AppConfiguration.fileData.size > i) {
                html.append("<tr>\n" + " <td>").append(AppConfiguration.fileData[i].getFilename()).append("</td>\n")
                        .append("    <td>").append(AppConfiguration.fileData[i].getFiletype()).append("</td>\n")
                        .append("    <td>").append(AppConfiguration.fileData[i].getFilepath()).append("</td>\n")
                        .append("    <td>").append(MainLayout.fileTable.columns[3].getCellData(i)).append("</td>\n")
                        .append("    <td>").append(AppConfiguration.fileData[i].getCreationDate()).append("</td>\n")
                        .append("    <td>").append(AppConfiguration.fileData[i].lastModif).append("</td>\n")
                        .append("    <td>").append(AppConfiguration.fileData[i].getLastAccessDate()).append("</td>\n")
                        .append("    <td>").append(AppConfiguration.fileData[i].getIsHidden()).append("</td>\n")
                        .append("</tr>\n")
                i++
            }
            html.append("</table>")
            return html.toString()
        }

    private fun getSeparatorLine(diffColumn: IntArray): String {

        val sb = StringBuilder()
        for (i in MainLayout.fileColumn.indices) {
            if (option[i]) {
                val sepLine = MainLayout.fileColumn[i]!!.text.length + diffColumn[i]
                sb.append("-".repeat(Math.max(0, sepLine)))
            }
        }
        return sb.toString()
    }

    fun calcColumnLenght() {
        for (i in 0 until AppConfiguration.fileData.size) {
            if (lenght[0] < AppConfiguration.fileData[i].getFilename().length) {
                lenght[0] = AppConfiguration.fileData[i].getFilename().length
            }
            if (lenght[1] < AppConfiguration.fileData[i].getFiletype().length) {
                lenght[1] = AppConfiguration.fileData[i].getFiletype().length
            }
            if (lenght[2] < AppConfiguration.fileData[i].getFilepath().length) {
                lenght[2] = AppConfiguration.fileData[i].getFilepath().length
            }
            if (lenght[3] < MainLayout.fileTable.columns[3].getCellData(i).toString().length) {
                lenght[3] = MainLayout.fileTable.columns[3].getCellData(i).toString().length
            }
            if (lenght[4] < AppConfiguration.fileData[i].getCreationDate().length) {
                lenght[4] = AppConfiguration.fileData[i].getCreationDate().length
            }
            if (lenght[5] < AppConfiguration.fileData[i].lastModif.length) {
                lenght[5] = AppConfiguration.fileData[i].lastModif.length
            }
            if (lenght[6] < AppConfiguration.fileData[i].getLastAccessDate().length) {
                lenght[6] = AppConfiguration.fileData[i].getLastAccessDate().length
            }
            if (lenght[7] < AppConfiguration.fileData[i].getIsHidden().length) {
                lenght[7] = AppConfiguration.fileData[i].getIsHidden().length
            }
        }
        //System.out.println("Longest filelength: " + lenght[0] + " " + lenght[1] + " " + lenght[2] + " " + lenght[3] + " " + lenght[4]);

        for (i in 0 until AppConfiguration.dirData.size) {
            if (dirlenght < AppConfiguration.dirData[i].getDir().length) {
                dirlenght = AppConfiguration.dirData[i].getDir().length
            }
        }
        //System.out.println("Longest dirlength: " + dirlenght);
    }

    /**
     * Saves the filelist to a text/html file
     */
    fun saveToFile(file: File, fileformatIDx: Int) {
        try {
            val fileWriter = FileWriter(file)
            when (fileformatIDx) {
                0 -> fileWriter.write(MainLayout.previewTA.text)
                1 -> fileWriter.write(html)
            }
            fileWriter.close()
        } catch (e1: IOException) {
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                ExceptionHandler(Thread.currentThread(), e1)
            }
        }
    }

    companion object {
        @JvmStatic
        var flController: FilelistController? = null
        var option = BooleanArray(MainLayout.fileColumn.size)
        var sortingIdx: Int = 0
        @JvmStatic
        var UNIT_IDX = 1
        var canShowHeader = true
        var canShowEmptyFolder = true
    }
}
