package net.rickiekarp.core.util.random

object RandomCharacter {

    private const val RANDOM_CHARACTER_LIST = "АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя"

    fun getCharacterAtIndex(index : Int) : Char {
        return RANDOM_CHARACTER_LIST[index % RANDOM_CHARACTER_LIST.length]
    }

    fun getIndexFromChar(character : Char) : Int {
        return RANDOM_CHARACTER_LIST.indexOf(character)
    }

    fun letterToAlphabetPos(letter: Char): Int {
        return (letter.uppercaseChar() - 64).code
    }

    fun alphabetPosToLetter(pos: Int): Char {
        return (pos + 64).toChar()
    }
}