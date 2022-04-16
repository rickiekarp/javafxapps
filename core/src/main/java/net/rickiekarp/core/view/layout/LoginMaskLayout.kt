package net.rickiekarp.core.view.layout

import net.rickiekarp.core.AppContext
import net.rickiekarp.core.account.Account
import net.rickiekarp.core.account.ILoginHandler
import net.rickiekarp.core.view.MainScene
import net.rickiekarp.core.view.MessageDialog
import net.rickiekarp.core.view.login.AccountScene
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*

/**
 * Main Login Mask layout class.
 */
class LoginMaskLayout {
    private val main: VBox
    private val grid: GridPane
    private val loginLabel: Label
    private val loginButton: Button
    private val registerButton: Button
    private var loadBar: ProgressBar? = null
    var loginTask: Task<Boolean>? = null
    private val username: TextField
    private val password: PasswordField
    private val rememberPass: CheckBox
    private val autoLogin: CheckBox

    val maskNode: Node
        get() = main

    init {
        main = VBox()
        main.spacing = 20.0
        main.alignment = Pos.CENTER
        grid = GridPane()
        //grid.setGridLinesVisible(true);
        grid.alignment = Pos.CENTER
        grid.hgap = 10.0
        grid.vgap = 10.0
        loginLabel = Label("Login")
        main.children.add(loginLabel)

        val usernameLabel = Label("User")
        grid.add(usernameLabel, 0, 1)

        username = TextField()
        grid.add(username, 1, 1)

        val passwordLabel = Label("Password")
        grid.add(passwordLabel, 0, 2)

        password = PasswordField()
        grid.add(password, 1, 2)

        rememberPass = CheckBox("Remember password?")
        grid.add(rememberPass, 0, 3)

        autoLogin = CheckBox("Auto login?")
        grid.add(autoLogin, 0, 4)

        loginButton = Button("Login")
        loginButton.setOnAction { arg0 ->
            if (!getUsername().isEmpty() && !getPassword().isEmpty()) {
                Thread(loginTask).start()
            } else {
                MessageDialog(0, "Enter all account details!", 400, 200)
            }
        }

        registerButton = Button("Register")
        registerButton.setOnAction { arg0 -> MainScene.mainScene.sceneViewStack.push(RegistrationLayout().maskNode) }

        main.children.add(grid)
        main.children.add(loginButton)
        main.children.add(registerButton)

        if (AppContext.context.accountManager.account != null) {
            username.text = AppContext.context.accountManager.account!!.user
            password.text = AppContext.context.accountManager.account!!.password
            rememberPass.isSelected = AppContext.context.accountManager.isRememberPass
            autoLogin.isSelected = AppContext.context.accountManager.isAutoLogin
        }

        loginTask = doLogin()
    }

    fun doLogin(): Task<Boolean> {
        val loginTask = object : Task<Boolean>() {
            override fun call(): Boolean? {
                try {
                    val result: Boolean?

                    loadBar = ProgressBar(0.0)
                    loadBar!!.progress = ProgressIndicator.INDETERMINATE_PROGRESS

                    Platform.runLater {
                        main.children.remove(registerButton)
                        main.children.remove(loginButton)
                        main.children.add(loadBar)
                        grid.isDisable = true
                    }

                    var account: Account? = AppContext.context.accountManager.account
                    if (account == null || account.user!!.isEmpty() || account.password!!.isEmpty()) {
                        account = Account(getUsername(), getPassword())
                    }

                    if (requestLogin(account)) {
                        AppContext.context.accountManager.account = account
                        result = java.lang.Boolean.TRUE
                    } else {
                        Platform.runLater { setStatus("Login not possible!") }
                        result = java.lang.Boolean.FALSE
                    }
                    Platform.runLater {
                        main.children.remove(loadBar)
                        main.children.add(loginButton)
                        main.children.add(registerButton)
                        grid.isDisable = false
                    }
                    return result
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }

            }
        }

        loginTask.setOnFailed { t ->
            // This handler will be called if exception occured during your task execution
            // E.g. network or db connection exceptions
            setStatus("Login failed!")
        }

        return loginTask
    }

    private fun requestLogin(account: Account?): Boolean {
        AppContext.context.accountManager.account = account
        if (AppContext.context.accountManager.updateAccessToken()) {
            AppContext.context.accountManager.createActiveProfile(rememberPass.isSelected, autoLogin.isSelected)
            //AppContext.getContext().getAccountManager().updateSession();
            return true
        } else {
            AppContext.context.accountManager.account = null
            return false
        }
    }

    private fun doLogout(ILoginHandler: ILoginHandler) {
        AppContext.context.accountManager.account = null

        this.loginTask = this.doLogin()
        ILoginHandler.setAppContextLoginBehaviour(this)

        //show login layout
        MainScene.mainScene.borderPane.top = null
        MainScene.mainScene.borderPane.center = this.maskNode

        //remove account menu from title bar
        MainScene.mainScene.windowScene!!.win.titlebarButtonBox.children.removeAt(MainScene.mainScene.windowScene!!.win.titlebarButtonBox.children.size - 1)
    }

    fun addAccountMenu(loginHandler: ILoginHandler) {
        val menuItem1 = MenuItem("Account")
        menuItem1.setOnAction { event -> AccountScene() }

        val menuItem2 = MenuItem("Logout")
        menuItem2.setOnAction { event ->
            doLogout(loginHandler)
            loginHandler.setOnLogout()
            MainScene.mainScene.windowScene!!.win.contentController!!.removeSidebarItemByIdentifier("pluginmanager")
        }

        val menuButton = MenuButton(AppContext.context.accountManager.account!!.user, null, menuItem1, menuItem2)
        MainScene.mainScene.windowScene!!.win.titlebarButtonBox.children.addAll(menuButton)
    }

    private fun getUsername(): String {
        return username.text
    }

    fun getPassword(): String {
        return password.text
    }

    fun setStatus(text: String) {
        loginLabel.text = text
    }
}
