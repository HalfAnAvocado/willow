package com.marvinelsen.willow.util

import com.marvinelsen.willow.WillowApplication
import java.io.InputStream

object PronunciationConverter {
    private val numberToToneMarkMapping = mapOf(1 to "", 2 to "ˊ", 3 to "ˇ", 4 to "ˋ", 5 to "˙")
    private val pinyinToZhuyinMapping =
        parseTranscriptions(WillowApplication::class.java.getResourceAsStream("data/pinyin_zhuyin_transcriptions.tsv")!!)

    fun convertToZhuyin(numberedPinyin: String) =
        numberedPinyin.split(" ")
            .joinToString(separator = "　", transform = PronunciationConverter::convertSyllableToZhuyin)

    private fun convertSyllableToZhuyin(numberedPinyinSyllable: String): String {
        val lastCharacter = numberedPinyinSyllable.last()
        val toneNumber = lastCharacter.digitToIntOrNull()
        val pinyinSyllable = if (lastCharacter.isDigit())
            numberedPinyinSyllable.substring(0..<numberedPinyinSyllable.lastIndex) else numberedPinyinSyllable

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