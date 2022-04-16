package net.rickiekarp.core.net

import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import java.io.*

class NetResponse {

    companion object {
        fun getResponseString(inputStream: InputStream?): String? {
            var inputLine: String
            val response = StringBuilder()

            val `in`: BufferedReader
            try {
                `in` = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return response.toString()
            } catch (e: NullPointerException) {
                return response.toString()
            }

            try {
                while (true) {
                    val line = `in`.readLine() ?: break
                    response.append(line)
                }
                `in`.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return response.toString()
            }

            return response.toString()
        }

        @Throws(IOException::class)
        fun getResponseString(r: Response): String? {
            val inputStream = r.body!!.byteStream()
            return getResponseString(inputStream)
        }

        private fun getResponseByteArray(inputStream: InputStream?): ByteArray {
            val bos = ByteArrayOutputStream()
            var len: Int
            val buffer = ByteArray(4096)
            try {
                val len = inputStream!!.read(buffer)
                while (-1 != (len)) {
                    bos.write(buffer, 0, len)
                }
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bos.toByteArray()
        }

        fun getResponseJson(inputStream: InputStream?): JSONObject? {
            val responseString = getResponseString(inputStream)
            try {
                return JSONObject(responseString)
            } catch (e: JSONException) {
                return null
            }
        }

        fun getResponseResultAsBoolean(`in`: InputStream?, key: String): Boolean {
            return getResponseJson(`in`)!!.getBoolean(key)
        }

        /**
         * Converts a given XML String to a DOM Object and returns the document
         */
        @Throws(SAXException::class, ParserConfigurationException::class, IOException::class)
        fun getResponseXml(inputStream: InputStream): Document {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            return builder.parse(InputSource(StringReader(getResponseString(inputStream))))
        }
    }



}