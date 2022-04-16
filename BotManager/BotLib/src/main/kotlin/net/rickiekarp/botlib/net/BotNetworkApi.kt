package net.rickiekarp.botlib.net

import net.rickiekarp.core.net.NetworkAction
import net.rickiekarp.core.net.NetworkApi
import net.rickiekarp.core.net.provider.NetworkParameterProvider
import net.rickiekarp.botlib.model.PluginData

class BotNetworkApi : NetworkApi() {
    companion object {
        private val AUTH_DOMAIN = "auth"
        private val PLUGIN_DOMAIN = "plugin"
        private val VALIDATE_ACTION = "validate"
        private val PLUGIN_DATA_ACTION = "data"
        private val PLUGIN_NAME_ACTION = "name"

        fun requestValidation(plugin: PluginData): NetworkAction {
            val provider = NetworkParameterProvider.create()
            provider.put("plugin", plugin.pluginName.get())
            return NetworkAction.Builder.create().setHost(NetworkAction.DATASERVER).setDomain(AUTH_DOMAIN).setAction(VALIDATE_ACTION).setParameters(provider).setMethod("POST").build()
        }

        fun requestPlugins(): NetworkAction {
            return NetworkAction.Builder.create().setHost(NetworkAction.DATASERVER).setDomain(PLUGIN_DOMAIN).setAction(PLUGIN_DATA_ACTION).setMethod("GET").build()
        }
    }
}
