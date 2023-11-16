package com.marvinelsen.willow.dictionary.objects

import com.marvinelsen.willow.persistence.entities.WordEntity

data class Word(
    val traditional: String,
    val simplified: String?,
    val definitions: Map<SourceDictionary, List<Definition>>,
)

fun WordEntity.asWord() = Word(
    traditional = traditional,
    simplified = simplified,
    definitions = definitions.map { it.asDefinition() }.groupBy { it.sourceDictionary })