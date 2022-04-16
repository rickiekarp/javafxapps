package net.rickiekarp.core

import net.rickiekarp.core.account.AccountManager
import net.rickiekarp.core.controller.LanguageController
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.net.NetworkApi
import net.rickiekarp.core.settings.Configuration
import net.rickiekarp.core.util.FileUtil

import java.io.IOException
import java.util.jar.JarFile
import java.util.jar.Manifest

class AppContext protected constructor(val contextIdentifier: String, val networkApi: NetworkApi) {
    var accountManager: AccountManager
        private set
    var internalVersion: String? = null

    val applicationName: String
        get() = LanguageController.getString(contextIdentifier)

    val versionNumber: String?
        get() {
            val manifest: Manifest
            try {
                manifest = JarFile(Configuration.config.jarFile.path).manifest
                return FileUtil.readManifestProperty(manifest, "Version")
            } catch (e: IOException) {
                return internalVersion
            }

        }

    init {
        accountManager = AccountManager()
    }

    fun initAccountManager() {
        accountManager.account = accountManager.loadAccountFromFile()
        LogFileHandler.logger.config("ACCOUNT=" + accountManager.account)
    }

    companion object {
        lateinit var context: AppContext
            private set

        fun create(identifier: String) {
            context = AppContext(identifier, NetworkApi())
        }

        fun create(identifier: String, network: NetworkApi) {
            context = AppContext(identifier, network)
        }
    }
}
