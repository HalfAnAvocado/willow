package com.marvinelsen.willow.sources.moe

import com.marvinelsen.willow.dictionary.Definition
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.util.PronunciationConverter

fun List<MoeEntry>.toEntries() = this.flatMap(MoeEntry::toEntries)

fun MoeEntry.toEntries(): List<Entry> {
    if (this.title.startsWith('{')) return emptyList()

    return this.heteronyms.filterNot { it.zhuyin.isNullOrBlank() }.map {
        Entry(
            traditional = this.title,
            zhuyin = it.zhuyin!!.replace("""（.*）""".toRegex(), ""),
            accentedPinyin = it.accentedPinyin?.replace("""（.*）""".toRegex(), "")
                ?: PronunciationConverter.convertToAccentedPinyin(it.zhuyin),
            definitions = listOf(it.dictionaryDefinition)
        )
    }
}

private val MoeHeteronym.dictionaryDefinition
    get() = Definition(
        shortDefinition = MoeDefinitionFormatter.formatShortDefinition(this),
        htmlDefinition = MoeDefinitionFormatter.formatHtmlDefinition(this),
        sourceDictionary = SourceDictionary.MOE
    )
