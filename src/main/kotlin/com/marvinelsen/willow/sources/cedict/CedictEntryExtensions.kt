package com.marvinelsen.willow.sources.cedict

import com.marvinelsen.willow.dictionary.Definition
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.util.PronunciationConverter

fun List<CedictEntry>.toEntries() = this.map(CedictEntry::toEntry)

fun CedictEntry.toEntry() = Entry(
    traditional = this.traditional,
    numberedPinyin = this.numberedPinyinTaiwan ?: this.numberedPinyin,
    zhuyin = PronunciationConverter.convertToZhuyin(this.numberedPinyinTaiwan ?: this.numberedPinyin),
    definitions = listOf(this.dictionaryDefinition)
)

private val CedictEntry.dictionaryDefinition
    get() = Definition(
        shortDefinition = CedictDefinitionFormatter.formatShortDefinition(this),
        htmlDefinition = CedictDefinitionFormatter.formatHtmlDefinition(this),
        sourceDictionary = SourceDictionary.CEDICT
    )
