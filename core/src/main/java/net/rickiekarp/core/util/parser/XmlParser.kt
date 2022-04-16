package net.rickiekarp.core.util.parser

import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import java.io.IOException
import java.io.StringReader

object XmlParser {
    /**
     * Converts a given XML String to a DOM Object and returns the document
     */
    @Throws(SAXException::class, ParserConfigurationException::class, IOException::class)
    fun stringToDom(xmlString: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.parse(InputSource(StringReader(xmlString)))
    }
}
