package com.marvinelsen.willow.dictionary.objects

data class Entry(
    val traditional: String,
    val zhuyin: String,
    val definitions: Map<SourceDictionary, List<Definition>>,
) {
    val preferredDefinitions: List<Definition> by lazy {
        definitions[SourceDictionary.LAC] ?: definitions[SourceDictionary.MOE] ?: definitions[SourceDictionary.CEDICT]!!
    }
}