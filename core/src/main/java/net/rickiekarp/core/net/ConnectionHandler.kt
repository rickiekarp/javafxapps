package net.rickiekarp.core.net

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.settings.Configuration
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.ConnectException
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * A handler to add a layer of abstraction between network layer and implementation
 */
internal class ConnectionHandler {
    private val mHttpClient: OkHttpClient

    init {
        mHttpClient = OkHttpClient.Builder()
                .connectTimeout(MAX_CONNECTION_TIMEOUT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(MAX_DATA_TRANSFER_TIMEOUT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
                .build()
    }

    fun requestInputStream(networkAction: NetworkAction): InputStream? {
        val response = request(networkAction) ?: return null
        return response.body!!.byteStream()
    }

    fun request(networkAction: NetworkAction): Response? {
        try {
            val hostUrl = URL(Configuration.host + networkAction.hostUrl)
            return performRequest(hostUrl, networkAction)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    @Throws(IOException::class)
    private fun performRequest(hostUrl: URL, action: NetworkAction): Response? {
        var decodedUrl = decodeUrlToString(hostUrl) + API_STRING + action.actionUrl

        val builder = Request.Builder()

        when (action.method) {
            "GET" -> {
                decodedUrl += encodeParametersToString(action.parameterMap)
                builder.get()
            }
            "POST" -> {
                val requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, encodeParametersToJson(action.parameterMap))
                builder.post(requestBody)
            }
        }

        val composedUrl = URL(decodedUrl)
        builder.url(composedUrl)

        addHeaders(action.parameterMap, builder)

        val request = builder.build()
        LogFileHandler.logger.info(request.method + ": " + decodedUrl)
        //printRequestHeaders(request);
        //printRequestBody(request);

        return try {
            val response = mHttpClient.newCall(request).execute()
            if (response.code == RESPONSE_ERROR_CODE_SERVER_UNAVAILABLE) {
                throw RuntimeException("Error")
            }
            //LogFileHandler.logger.info(String.valueOf(response.code()));
            //printResponseHeaders(response);
            response
        } catch (e: ConnectException) {
            LogFileHandler.logger.severe(e.message)
            null
        }

    }

    private fun addHeaders(parameterMap: Map<String, Any>, builder: Request.Builder) {
        builder.addHeader(HEADER_USER_AGENT, AppContext.context.contextIdentifier + "/" + AppContext.context.internalVersion)

        val accountManager = AppContext.context.accountManager
        if (accountManager.account != null) {
            if (accountManager.account!!.accessToken != null) {
                builder.addHeader(HEADER_AUTHORIZATION, "Basic " + accountManager.account!!.accessToken!!)
            }
        }
    }

    /**
     * Puts the parameters of the given parameter map into a JSON Object and returns its string representation
     * @param params Parameter map to encode
     * @return JSON String of the parameter map
     */
    private fun encodeParametersToJson(params: Map<String, Any>): String {
        val resultJson = JSONObject()
        for ((key, value) in params) {
            resultJson.put(key, value)
        }
        return resultJson.toString()
    }

    /**
     * Encodes the given parameter map to a URL parameter representation
     * @param params Parameter map to encode
     * @return URL representation of the parameter map
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    private fun encodeParametersToString(params: Map<String, Any>): String {
        val result = StringBuilder()
        var isFirst = true

        for ((key, value) in params) {
            if (isFirst) {
                isFirst = false
                result.append('?')
            } else {
                result.append('&')
            }

            result.append(URLEncoder.encode(key, UTF_8))
            result.append('=')
            result.append(URLEncoder.encode(value.toString(), UTF_8))
        }
        return result.toString()
    }

    private fun decodeUrlToString(url: URL): String {
        val port: String
        val portInt = url.port
        if (portInt != NO_PORT) {
            port = ":$portInt"
        } else {
            port = ""
        }
        return url.protocol + "://" + url.host + port + url.path
    }

    private fun bodyToString(request: Request): String? {
        //		try {
        //			final Request copy = request.newBuilder().build();
        //			final Buffer buffer = new Buffer();
        //			copy.body().writeTo(buffer);
        //			return buffer.readUtf8();
        //		} catch (final IOException e) {
        //			e.printStackTrace();
        //			return null;
        //		}
        return null
    }

    private fun printRequestBody(request: Request) {
        if (request.method == "POST") {
            println()
            println("Request body:")
            println(bodyToString(request))
            println()
        }
    }

    private fun printRequestHeaders(request: Request) {
        println()
        println("Request Headers:")
        for (i in 0 until request.headers.size) {
            println(request.headers.name(i) + ": " + request.headers.value(i))
        }
        println()
    }

    private fun printResponseHeaders(response: Response) {
        println()
        println("Response Headers:")
        for (i in 0 until response.headers.size) {
            println(response.headers.name(i) + ": " + response.headers.value(i))
        }
        println()
    }

    companion object {
        private val API_STRING = "/"
        private val MEDIA_TYPE_MARKDOWN = "application/json".toMediaTypeOrNull()
        private val MAX_CONNECTION_TIMEOUT_MILLISECONDS = 15000
        private val MAX_DATA_TRANSFER_TIMEOUT_MILLISECONDS = 40000
        private val NO_PORT = -1
        private val UTF_8 = "UTF-8"
        private val HEADER_USER_AGENT = "User-Agent"
        private val HEADER_AUTHORIZATION = "Authorization"
        private val RESPONSE_ERROR_CODE_SERVER_UNAVAILABLE = 503
    }
}
