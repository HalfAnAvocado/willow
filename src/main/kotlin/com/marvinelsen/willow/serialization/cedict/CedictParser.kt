package com.marvinelsen.willow.serialization.cedict

import java.io.InputStream

// CC-CEDICT format: https://cc-cedict.org/wiki/format:syntax
// Traditional Simplified [pin1 yin1] /gloss; gloss; .../gloss; gloss; .../
// 皮實 皮实 [pi2 shi5] /(of things) durable/(of people) sturdy; tough/
private val cedictEntryRegex =
    """^(?<traditional>\S+) (?<simplified>\S+) \[(?<pinyin>[a-zA-Z0-9:,· ]+)] /(?<definitions>.+)/$""".toRegex()
private val taiwanPronunciationRegex = """Taiwan pr. \[(?<pinyin>\S+)]""".toRegex()

object CedictParser {
    fun parse(inputStream: InputStream) =
        inputStream.bufferedReader()
            .lineSequence()
            .filter { !it.startsWith("#") }
            .map { it.toCedictEntry() }
            .toList()
}

private fun String.toCedictEntry(): CedictEntry {
    val matchResult = cedictEntryRegex.matchEntire(this)!!
    val (traditional, simplified, numberedPinyin, definitions) = matchResult.destructured
    val numberedPinyinTaiwan = taiwanPronunciationRegex.find(this)?.groups?.get("pinyin")?.value
    return CedictEntry(
        traditional = traditional,
        simplified = simplified,
        numberedPinyin = numberedPinyin.lowercase(),
        numberedPinyinTaiwan = numberedPinyinTaiwan?.lowercase(),
        definitions = definitions.split("/").filter { !it.contains("Taiwan pr. ") }.joinToString(separator = "/"),
    )
}