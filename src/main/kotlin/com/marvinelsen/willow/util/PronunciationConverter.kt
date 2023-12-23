package com.marvinelsen.willow.util

import com.marvinelsen.willow.WillowApplication
import java.io.InputStream

@Suppress("MagicNumber")
object PronunciationConverter {
    const val ZHUYIN_SEPARATOR = "　"

    private val numberToToneMarkMapping = mapOf(1 to "", 2 to "ˊ", 3 to "ˇ", 4 to "ˋ", 5 to "˙")
    private val toneMarkToNumberMapping = mapOf('ˊ' to 2, 'ˇ' to 3, 'ˋ' to 4)
    private val pinyinToZhuyinMapping =
        parseTranscriptions(
            WillowApplication::class.java.getResourceAsStream("data/pinyin_zhuyin_transcriptions.tsv")!!
        )
    private val zhuyinToPinyinMapping = pinyinToZhuyinMapping.entries.associate { it.value to it.key }

    fun convertToNumberedPinyin(zhuyin: String) = zhuyin
        .split(ZHUYIN_SEPARATOR)
        .joinToString(separator = " ", transform = PronunciationConverter::convertSyllableToNumberedPinyin)

    private fun convertSyllableToNumberedPinyin(zhuyinSyllable: String): String {
        val tone = when {
            zhuyinSyllable.first() == '˙' -> 5
            else -> toneMarkToNumberMapping[zhuyinSyllable.last()] ?: 1
        }

        val zhuyinSyllableWithoutToneMark = when (tone) {
            1 -> zhuyinSyllable
            5 -> zhuyinSyllable.substring(1)
            else -> zhuyinSyllable.substring(0..<zhuyinSyllable.lastIndex)
        }

        return zhuyinToPinyinMapping[zhuyinSyllableWithoutToneMark] + tone
    }

    fun convertToZhuyin(numberedPinyin: String) = numberedPinyin
        .split(" ")
        .joinToString(separator = ZHUYIN_SEPARATOR, transform = PronunciationConverter::convertSyllableToZhuyin)

    private fun convertSyllableToZhuyin(numberedPinyinSyllable: String): String {
        val lastCharacter = numberedPinyinSyllable.last()
        val toneNumber = lastCharacter.digitToIntOrNull()
        val pinyinSyllable = if (lastCharacter.isDigit()) {
            numberedPinyinSyllable.substring(0..<numberedPinyinSyllable.lastIndex)
        } else {
            numberedPinyinSyllable
        }

        val zhuyinSyllable = pinyinToZhuyinMapping[pinyinSyllable] ?: pinyinSyllable
        val toneMark = numberToToneMarkMapping[toneNumber] ?: ""

        return if (toneNumber == 5) toneMark + zhuyinSyllable else zhuyinSyllable + toneMark
    }

    private fun parseTranscriptions(inputStream: InputStream) =
        inputStream.bufferedReader()
            .readLines()
            .map { it.split('\t') }
            .associate { it[0] to it[1] }
}
