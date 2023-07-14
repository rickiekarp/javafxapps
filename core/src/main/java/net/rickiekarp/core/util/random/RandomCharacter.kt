package net.rickiekarp.core.util.random

object RandomCharacter {

    private const val RANDOM_CHARACTER_LIST = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"

    fun getCharacterAtIndex(index : Int) : Char {
        return RANDOM_CHARACTER_LIST[index % RANDOM_CHARACTER_LIST.length]
    }
}