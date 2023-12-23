package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.util.PronunciationConverter

data class Entry(
    val traditional: String,
    val zhuyin: String,
    val accentedPinyin: String = PronunciationConverter.convertToAccentedPinyin(zhuyin),
    val numberedPinyin: String = PronunciationConverter.convertToNumberedPinyin(zhuyin),
    val definitions: Map<SourceDictionary, List<Definition>>,
) {
    val characters: List<String> by lazy { traditional.split("") }
    val zhuyinSyllables: List<String> by lazy { zhuyin.split(PronunciationConverter.ZHUYIN_SEPARATOR) }
    val availableDefinitionSources = definitions.keys.sorted()
}

data class Definition(
    val shortDefinition: String,
    val htmlDefinition: String,
    val sourceDictionary: SourceDictionary,
)

data class Sentence(
    val traditional: String,
)

enum class SourceDictionary {
    USER, CEDICT, MOE, LAC
}

enum class SentenceSource {
    USER, TATOEBA
}
