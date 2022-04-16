package net.rickiekarp.core.net

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.debug.DebugHelper
import net.rickiekarp.core.debug.ExceptionHandler
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.settings.LoadSave
import javafx.collections.ObservableList

import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.HashMap

class NetworkAction internal constructor(builder: Builder) {
    internal val method: String? = builder.mMethod
    internal val parameterMap: Map<String, Any> = if (builder.mParameters != null) {
        builder.mParameters!!.parameters
    } else HashMap(0)
    internal val hostUrl: String? = builder.mHostURL
    internal val actionUrl: String = builder.mDomain + "/" + builder.mAction

    @Deprecated("")
    @get:Deprecated("")
    internal val file: String? = builder.mFile

    interface IParameterProvider {
        val parameters: Map<String, Any>
    }

    /**
     * @return Use ConnectionHandler class instead
     */
    @Deprecated("")
    fun requestStringFromResponse(): String {
        var programURL: URL? = null
        try {
            programURL = URL(Configuration.host + "files/apps/" + AppContext.context.contextIdentifier + "/" + file)

            val con = programURL.openConnection() as HttpURLConnection
            con.doOutput = false
            con.connectTimeout = 5000
            con.readTimeout = 20000
            con.setRequestProperty("User-Agent", AppContext.context.contextIdentifier + "-" + AppContext.context.internalVersion)
            con.requestMethod = method!!

            val `in` = BufferedInputStream(con.inputStream)
            val responseCode = con.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                LogFileHandler.logger.info("Server connection response: $responseCode (OK)")
            } else {
                LogFileHandler.logger.info("Server connection response: $responseCode")
            }

            val builder = StringBuilder()
            while ((`in`.read()) != -1) {
                val g = `in`.read().toChar()
                builder.append(g)
            }
            val xmlSource = builder.toString()
            con.disconnect()

            return xmlSource
        } catch (e: SocketTimeoutException) {
            LogFileHandler.logger.warning("Connection timed out!")
            if (DebugHelper.DEBUGVERSION) {
                e.printStackTrace()
            } else {
                LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e))
            }
        } catch (e1: IOException) {
            LogFileHandler.logger.warning("Can not connect to: " + programURL!!)
            if (DebugHelper.DEBUGVERSION) {
                e1.printStackTrace()
            } else {
                LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e1))
            }
            return "file_not_found"
        }

        return "no_connection"
    }

    /**
     * General NetworkAction Builder class
     */
    class Builder {
        var mHostURL: String? = null
        var mDomain: String? = null
        var mAction: String? = null
        var mParameters: IParameterProvider? = null
        var mMethod: String? = null

        @Deprecated("")
        var mFile: String? = null

        fun setHost(host: String): Builder {
            mHostURL = host
            return this
        }

        fun setDomain(domain: String): Builder {
            mDomain = domain
            return this
        }

        fun setAction(action: String): Builder {
            mAction = action
            return this
        }

        fun setParameters(parameterProvider: IParameterProvider): Builder {
            mParameters = parameterProvider
            return this
        }

        fun setMethod(method: String): Builder {
            mMethod = method
            return this
        }

        @Deprecated("")
        fun setFile(file: String): Builder {
            mFile = file
            return this
        }

        fun build(): NetworkAction {
            if (mHostURL == null) {
                LogFileHandler.logger.warning("No host was found, trying default: " + LoadSave.host)
                mHostURL = LoadSave.host
            }
            return NetworkAction(this)
        }

        companion object {

            fun create(): Builder {
                return Builder()
            }
        }
    }

    companion object {
        internal val LOGINSERVER = "LoginServer"
        val DATASERVER = "HomeServer"

        @Deprecated("")
        var hostList: ObservableList<String>? = null

        /**
         * Checks one of the listed servers for a new program version
         * If the connection to one server fails, another one is tried
         * @return Response as a string
         */
        @Deprecated("Use ConnectionHandler class instead")
        fun sendRequest(filename: String): String {
            var programURL: URL? = null
            var i = 0
            while (i < hostList!!.size) {
                try {
                    programURL = URL(hostList!![i] + filename)

                    val con = programURL.openConnection() as HttpURLConnection
                    con.doOutput = false
                    con.connectTimeout = 5000
                    con.readTimeout = 20000
                    con.setRequestProperty("User-Agent", AppContext.context.contextIdentifier + "-" + AppContext.context.internalVersion)
                    con.requestMethod = "GET"

                    val `in` = BufferedInputStream(con.inputStream)
                    val responseCode = con.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        LogFileHandler.logger.info("Server connection response: $responseCode (OK)")
                    } else {
                        LogFileHandler.logger.info("Server connection response: $responseCode")
                    }

                    val builder = StringBuilder()
                    while ((`in`.read()) != -1) {
                        val g = `in`.read().toChar()
                        builder.append(g)
                    }
                    val xmlSource = builder.toString()
                    con.disconnect()

                    //set the host server to download the updates from
                    //hostToUse = i;

                    return xmlSource
                } catch (e: SocketTimeoutException) {
                    LogFileHandler.logger.warning("Connection timed out!")
                    if (DebugHelper.DEBUGVERSION) {
                        e.printStackTrace()
                    } else {
                        LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e))
                    }
                } catch (e1: IOException) {
                    LogFileHandler.logger.warning("Can not connect to: " + programURL!!)
                    if (DebugHelper.DEBUGVERSION) {
                        e1.printStackTrace()
                    } else {
                        LogFileHandler.logger.warning(ExceptionHandler.getExceptionString(e1))
                    }
                    if (i++ == hostList!!.size) {
                        return "file_not_found"
                    }
                }

                i++
            }
            return "no_connection"
        }
    }
}
