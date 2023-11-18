package com.marvinelsen.willow.dictionary.objects

import com.marvinelsen.willow.persistence.entities.DefinitionEntity

data class Definition(
    val content: String,
    val sourceDictionary: SourceDictionary,
)

fun DefinitionEntity.asDefinition() = Definition(
    content = content,
    sourceDictionary = dictionary,
)
