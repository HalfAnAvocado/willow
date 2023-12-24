package com.marvinelsen.willow.sources.lac

import com.marvinelsen.willow.dictionary.Definition
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary

fun List<LacEntry>.toEntries() = this.map(LacEntry::toEntry)

fun LacEntry.toEntry() = Entry(
    traditional = this.traditional,
    zhuyin = this.zhuyinTaiwan.ifBlank { this.zhuyinMainland },
    definitions = listOf(this.dictionaryDefinition)
)

private val LacEntry.dictionaryDefinition
    get() = Definition(
        shortDefinition = LacDefinitionFormatter.formatShortDefinition(this),
        htmlDefinition = LacDefinitionFormatter.formatHtmlDefinition(this),
        sourceDictionary = SourceDictionary.LAC
    )
