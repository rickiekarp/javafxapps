package net.rickiekarp.core.view.layout

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.account.Account
import net.rickiekarp.core.debug.LogFileHandler
import net.rickiekarp.core.net.NetResponse
import net.rickiekarp.core.net.NetworkApi
import net.rickiekarp.core.view.MessageDialog
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox

import java.util.logging.Level

/**
 * Main Login Mask layout class.
 */
internal class RegistrationLayout {
    private val main: VBox = VBox()
    private val grid: GridPane
    private val loginLabel: Label
    private val username: TextField
    private val password: PasswordField

    val maskNode: Node
        get() = main

    init {
        main.spacing = 20.0
        main.alignment = Pos.CENTER
        grid = GridPane()
        //grid.setGridLinesVisible(true);
        grid.alignment = Pos.CENTER
        grid.hgap = 10.0
        grid.vgap = 10.0
        loginLabel = Label("Register")
        main.children.add(loginLabel)

        val usernameLabel = Label("User")
        grid.add(usernameLabel, 0, 1)

        username = TextField()
        grid.add(username, 1, 1)

        val passwordLabel = Label("Password")
        grid.add(passwordLabel, 0, 2)

        password = PasswordField()
        grid.add(password, 1, 2)

        val registerButton = Button("Submit")
        registerButton.setOnAction { arg0 ->
            if (!username.text.isEmpty() && !password.text.isEmpty()) {
                val account = Account(username.text, password.text)
                val inputStream = AppContext.context.networkApi.runNetworkAction(NetworkApi.requestCreateAccount(account))
                val responseJson = NetResponse.getResponseJson(inputStream)
            } else {
                MessageDialog(0, "Enter account details!", 400, 200)
            }
        }

        main.children.add(grid)
        main.children.add(registerButton)
    }

    private fun requestLogin(account: Account): Boolean {
        val tokenAction = AppContext.context.networkApi.requestResponse(
                NetworkApi.requestAccessToken(account)
        )

        LogFileHandler.logger.log(Level.INFO, tokenAction!!.code.toString())
        return when (tokenAction.code) {
            200 -> {
                AppContext.context.accountManager.account = account
                true
            }
            else -> false
        }
    }
}
