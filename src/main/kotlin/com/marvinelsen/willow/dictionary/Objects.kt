package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.util.PronunciationConverter

data class Entry(
    val traditional: String,
    val zhuyin: String,
    val definitions: Map<SourceDictionary, List<Definition>>,
) {
    val characters: List<String> by lazy { traditional.split("") }
    val zhuyinSyllables: List<String> by lazy { zhuyin.split(PronunciationConverter.ZHUYIN_SEPARATOR) }
    val preferredDefinitions: List<Definition> by lazy {
        definitions[SourceDictionary.LAC] ?: definitions[SourceDictionary.MOE] ?: definitions[SourceDictionary.CEDICT]!!
    }
    val availableDefinitionSources = definitions.keys
}

data class Definition(
    val shortDefinition: String,
    val htmlDefinition: String,
    val sourceDictionary: SourceDictionary,
)

enum class SourceDictionary {
    CEDICT, MOE, LAC
}