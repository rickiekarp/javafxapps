package net.rickiekarp.core.util.crypt.custom

import net.rickiekarp.core.extensions.addCharAtIndex
import net.rickiekarp.core.extensions.removeCharAtIndex
import net.rickiekarp.core.util.crypt.Md5Coder
import net.rickiekarp.core.util.math.MathUtil
import net.rickiekarp.core.util.random.RandomCharacter

object CustomCoderV1 {

    fun encode(input: String, baseSeed: String) : String {
        var outputText = ""
        val computedSeed = RandomCharacter.getMd5Seed(baseSeed)
        val shuffledCharacters = RandomCharacter.getCharacterListShuffled(computedSeed)

        var inputText = input
        inputText = inputText.trim().replace("[^a-zA-Z0-9]".toRegex(), "")

        var index = 0
        for (character in inputText) {
            val seedCharacterAsInt = RandomCharacter.getCharacterFromSeed(index, computedSeed)
            val outChar = RandomCharacter.getCharacterAtIndex(RandomCharacter.letterToAlphabetPos(character) + seedCharacterAsInt, shuffledCharacters)
            outputText += outChar.toString()
            index++
        }

        val numberOfCharsToAdd = MathUtil.log2(baseSeed.length, 0)
        val md5 = Md5Coder.calcMd5(baseSeed).replace("[^1-9]".toRegex(), "").substring(0, numberOfCharsToAdd)

        for (md5Digit in md5.toSortedSet().sorted()) {
            val randomCharacter = RandomCharacter.getRandomCharacter()
            outputText = outputText.addCharAtIndex(randomCharacter, md5Digit.digitToInt())
        }

        return outputText
    }


    fun decode(input: String, baseSeed: String) : String {
        var outputText = ""

        val seed = RandomCharacter.getMd5Seed(baseSeed)
        val shuffledCharacters = RandomCharacter.getCharacterListShuffled(seed);

        var inputText = input

        val numberOfCharsToAdd = MathUtil.log2(baseSeed.length, 0)
        val md5 = Md5Coder.calcMd5(baseSeed).replace("[^1-9]".toRegex(), "").substring(0, numberOfCharsToAdd)

        for (md5Digit in md5.toSortedSet().sortedDescending()) {
            inputText = inputText.removeCharAtIndex(md5Digit.digitToInt())
        }

        var index = 0
        for (character in inputText) {
            val seedCharacterAsInt = RandomCharacter.getCharacterFromSeed(index, seed)
            val characterIndex = RandomCharacter.getIndexFromChar(character, shuffledCharacters) - seedCharacterAsInt
            val decodedChar = RandomCharacter.alphabetPosToLetter(characterIndex)
            outputText += decodedChar.toString()
            index++
        }

        return outputText
    }
}