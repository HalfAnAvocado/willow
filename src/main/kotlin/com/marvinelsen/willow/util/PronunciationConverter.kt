package com.marvinelsen.willow.util

import com.marvinelsen.willow.WillowApplication
import java.io.InputStream

@Suppress("MagicNumber")
object PronunciationConverter {
    const val ZHUYIN_SEPARATOR = "　"

    private val numberToPinyinToneMarkMapping = mapOf(
        1 to "\u0304",
        2 to "\u0301",
        3 to "\u030C",
        4 to "\u0300",
        5 to ""
    )

    private val numberToZhuyinToneMarkMapping = mapOf(
        1 to "",
        2 to "ˊ",
        3 to "ˇ",
        4 to "ˋ",
        5 to "˙"
    )

    private val zhuyinToneMarkToNumberMapping = mapOf(
        'ˊ' to 2,
        'ˇ' to 3,
        'ˋ' to 4,
        '˙' to 5
    )

    private val pinyinToZhuyinMapping = parseTranscriptions(
        WillowApplication::class.java.getResourceAsStream("data/pinyin_zhuyin_transcriptions.tsv")!!
    )
    private val zhuyinToPinyinMapping = pinyinToZhuyinMapping.entries.associate { it.value to it.key }

    fun convertToZhuyin(numberedPinyin: String) = numberedPinyin
        .split(" ")
        .joinToString(separator = ZHUYIN_SEPARATOR, transform = PronunciationConverter::convertSyllableToZhuyin)

    fun convertToNumberedPinyin(zhuyin: String) = zhuyin
        .split(ZHUYIN_SEPARATOR)
        .joinToString(separator = " ", transform = PronunciationConverter::convertSyllableToNumberedPinyin)

    fun convertToAccentedPinyin(zhuyin: String) = zhuyin
        .split(ZHUYIN_SEPARATOR)
        .joinToString(separator = " ", transform = PronunciationConverter::convertSyllableToAccentedPinyin)

    private fun convertSyllableToAccentedPinyin(zhuyinSyllable: String): String {
        val pinyinSyllable = zhuyinToPinyinMapping[zhuyinSyllable.stripToneMarks()]
            ?: throw IllegalArgumentException("Invalid zhuyin syllable $zhuyinSyllable")

        val characterToIndex = pinyinSyllable.withIndex().associate { it.value to it.index }
        val vowelIndex = when {
            'a' in characterToIndex -> characterToIndex['a']!!
            'o' in characterToIndex -> characterToIndex['o']!!
            'e' in characterToIndex -> characterToIndex['e']!!
            'i' in characterToIndex ->
                if (pinyinSyllable.elementAtOrNull(characterToIndex['i']!! + 1) == 'u') {
                    characterToIndex['u']!!
                } else {
                    characterToIndex['i']!!
                }

            'u' in characterToIndex -> characterToIndex['u']!!
            'ü' in characterToIndex -> characterToIndex['ü']!!
            else -> error("Missing vowel in pinyin syllable $pinyinSyllable from zhuyin $zhuyinSyllable")
        }

        return buildString {
            append(pinyinSyllable)
            insert(vowelIndex + 1, numberToPinyinToneMarkMapping[zhuyinSyllable.tone])
        }
    }

    private fun convertSyllableToNumberedPinyin(zhuyinSyllable: String) =
        zhuyinToPinyinMapping[zhuyinSyllable.stripToneMarks()] + zhuyinSyllable.tone

    private fun convertSyllableToZhuyin(numberedPinyinSyllable: String): String {
        val lastCharacter = numberedPinyinSyllable.last()
        val toneNumber = lastCharacter.digitToIntOrNull()
        val pinyinSyllable = if (lastCharacter.isDigit()) {
            numberedPinyinSyllable.substring(0..<numberedPinyinSyllable.lastIndex)
        } else {
            numberedPinyinSyllable
        }

        val zhuyinSyllable = pinyinToZhuyinMapping[pinyinSyllable] ?: pinyinSyllable
        val toneMark = numberToZhuyinToneMarkMapping[toneNumber] ?: ""

        return if (toneNumber == 5) toneMark + zhuyinSyllable else zhuyinSyllable + toneMark
    }

    private fun parseTranscriptions(inputStream: InputStream) =
        inputStream.bufferedReader()
            .readLines()
            .map { it.split('\t') }
            .associate { it[0] to it[1] }

    private val String.tone: Int
        get() = when {
            this.first() == '˙' -> 5
            else -> zhuyinToneMarkToNumberMapping[this.last()] ?: 1
        }

    private fun String.stripToneMarks() = when (this.tone) {
        1 -> this
        5 -> this.substring(1)
        else -> this.substring(0..<this.lastIndex)
    }
}
