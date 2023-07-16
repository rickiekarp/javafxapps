package net.rickiekarp.core.util.random

import net.rickiekarp.core.util.crypt.Md5Coder
import okhttp3.internal.toImmutableList
import java.util.*

object RandomCharacter {

    private const val CYRILLIC_ALPHABET = "АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя"

    fun getSeed(inputData : String) : Long {
        var md5 = Md5Coder.calcMd5(inputData);
        md5 = customMd5Replacer(md5)
        return md5.substring(0,16).toLong()
    }

    private fun customMd5Replacer(md5: String): String {
        var modifiedMd5 = md5
        modifiedMd5 = modifiedMd5.replace("a", "2")
        modifiedMd5 = modifiedMd5.replace("b", "5")
        modifiedMd5 = modifiedMd5.replace("c", "3")
        modifiedMd5 = modifiedMd5.replace("d", "7")
        modifiedMd5 = modifiedMd5.replace("e", "1")
        modifiedMd5 = modifiedMd5.replace("f", "4")
        return modifiedMd5
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

    fun letterToAlphabetPos(letter: Char, characterShift : Int = 64): Int {
        return (letter.uppercaseChar() - characterShift).code
    }

    fun getCharacterFromSeed(index : Int, seed : Long = Long.MIN_VALUE) : Int {
        val seedString = seed.toString()
        return seedString[index % seedString.length].digitToInt()
    }

    fun alphabetPosToLetter(pos: Int, characterShift : Int = 64): Char {
        return (pos + characterShift).toChar()
    }
}