package net.rickiekarp.core.components.textfield

import javafx.scene.control.skin.TextFieldSkin

class CustomTextFieldSkin(passwordField: CustomTextField) : TextFieldSkin(passwordField) {

    override fun maskText(txt: String): String {
        val textField = skinnable

        val n = textField.length
        val passwordBuilder = StringBuilder(n)
        for (i in 0 until n) {
            val bullet = '\u2022'
            passwordBuilder.append(bullet)
        }

        return passwordBuilder.toString()
    }
}