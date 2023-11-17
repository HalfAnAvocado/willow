package com.marvinelsen.willow.dictionary.objects

import com.marvinelsen.willow.persistence.entities.WordEntity

data class Word(
    val traditional: String,
    val simplified: String?,
    val definitions: Map<SourceDictionary, List<Definition>>,
) {
    val preferredDefinitions: List<Definition> by lazy {
        definitions[SourceDictionary.CEDICT] ?: definitions[SourceDictionary.LAC] ?: definitions[SourceDictionary.MOE]!!
    }
}

fun WordEntity.asWord() = Word(
    traditional = traditional,
    simplified = simplified,
    definitions = definitions.map { it.asDefinition() }.groupBy { it.sourceDictionary })