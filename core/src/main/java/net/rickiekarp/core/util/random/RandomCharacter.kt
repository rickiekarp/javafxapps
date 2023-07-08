package net.rickiekarp.core.util.random

import java.util.*

object RandomCharacter {

    private const val RANDOM_CHARACTER_LIST = "АББВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"

    fun getCharacterAtIndex(index : Int) : Char {
        return RANDOM_CHARACTER_LIST[index]
    }
}