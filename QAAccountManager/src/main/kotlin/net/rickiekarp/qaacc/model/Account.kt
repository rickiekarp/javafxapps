package net.rickiekarp.qaacc.model

import net.rickiekarp.qaacc.view.AccountEditDialog
import javafx.beans.property.SimpleStringProperty

class Account(aName: String, aMail: String, aLevel: String, aAlli: String) {
    private val name: SimpleStringProperty = SimpleStringProperty(aName)
    private val mail: SimpleStringProperty = SimpleStringProperty(aMail)
    private val level: SimpleStringProperty = SimpleStringProperty(aLevel)
    private val alliance: SimpleStringProperty = SimpleStringProperty(aAlli)

    var accName: String
        get() = name.get()
        set(aName) = name.set(aName)

    var accMail: String
        get() = mail.get()
        set(aMail) = mail.set(aMail)

    var accLevel: String
        get() = level.get()
        set(aLevel) = level.set(aLevel)

    var accAlliance: String
        get() = alliance.get()
        set(aAlli) = alliance.set(aAlli)

    companion object {
        fun setAccount(acc: Account) {
            acc.accName = AccountEditDialog.accNameTF.text
            acc.accMail = AccountEditDialog.accMailTF.text
            acc.accLevel = AccountEditDialog.accLevelTF.text
            acc.accAlliance = AccountEditDialog.accAllianceTF.text
        }

        fun showAccountDetails(acc: Account) {
            AccountEditDialog.accNameTF.text = acc.accName
            AccountEditDialog.accMailTF.text = acc.accMail
            AccountEditDialog.accLevelTF.text = acc.accLevel
            AccountEditDialog.accAllianceTF.text = acc.accAlliance
        }
    }
}
