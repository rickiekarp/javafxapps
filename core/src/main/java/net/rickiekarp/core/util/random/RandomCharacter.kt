package net.rickiekarp.core.util.random

import net.rickiekarp.core.util.crypt.Md5Coder
import okhttp3.internal.toImmutableList
import java.util.*

object RandomCharacter {

    private const val CYRILLIC_ALPHABET = "АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя"

    fun getSeed(inputData : String) : Long {
        return Md5Coder.calcMd5(inputData).replace("[^0-9]".toRegex(), "").substring(0,16).toLong()
    }

    fun getCharacterListShuffled(seed : Long) : List<Char> {
        val characterList = CYRILLIC_ALPHABET.toMutableList()
        characterList.shuffle(Random(seed));
        return characterList.toImmutableList()
    }

    fun getCharacterAtIndex(index : Int, characterList : List<Char>) : Char {
        return characterList[index % characterList.size]
    }

    fun getCharacterAtIndex(index : Int, seed : Long = Long.MIN_VALUE) : Char {
        val characterList = getCharacterListShuffled(seed)
        return characterList[index % characterList.size]
    }

    fun getIndexFromChar(character : Char, characterList : List<Char>) : Int {
        return characterList.indexOf(character)
    }

    fun getRandomCharacter() : Char {
        return CYRILLIC_ALPHABET[(CYRILLIC_ALPHABET.indices).random()]
    }

    fun letterToAlphabetPos(letter: Char): Int {
        return (letter.uppercaseChar() - 64).code
    }

    fun alphabetPosToLetter(pos: Int): Char {
        return (pos + 64).toChar()
    }
}