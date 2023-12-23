package com.marvinelsen.willow.sources.cedict

import com.marvinelsen.willow.sources.common.Parser
import java.io.InputStream

// CC-CEDICT format: https://cc-cedict.org/wiki/format:syntax
// Traditional Simplified [pin1 yin1] /gloss; gloss; .../gloss; gloss; .../
// 皮實 皮实 [pi2 shi5] /(of things) durable/(of people) sturdy; tough/
private val cedictEntryRegex =
    """^(?<traditional>\S+) (?<simplified>\S+) \[(?<numberedPinyin>[a-zA-Z0-9:,· ]+)] /(?<definition>.+)/$""".toRegex()
private val taiwanPronunciationRegex = """Taiwan pr. \[(?<numberedPinyin>\S+)]""".toRegex()

object CedictParser : Parser<CedictEntry> {
    override fun parse(inputStream: InputStream) =
        inputStream.bufferedReader()
            .lineSequence()
            .filter { !it.startsWith("#") }
            .map(String::toCedictEntry)
            .toList()
}

@Suppress("DestructuringDeclarationWithTooManyEntries")
private fun String.toCedictEntry(): CedictEntry {
    val matchResult = cedictEntryRegex.matchEntire(this)!!
    val (traditional, simplified, numberedPinyin, definition) = matchResult.destructured
    val numberedPinyinTaiwan = taiwanPronunciationRegex.find(this)?.groups?.get("numberedPinyin")?.value

    return CedictEntry(
        traditional = traditional,
        simplified = simplified,
        numberedPinyin = numberedPinyin.lowercase().replace(" ,", ""),
        numberedPinyinTaiwan = numberedPinyinTaiwan?.lowercase(),
        definition = definition,
    )
}
