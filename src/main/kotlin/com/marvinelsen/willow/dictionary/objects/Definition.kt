package com.marvinelsen.willow.dictionary.objects

import com.marvinelsen.willow.persistence.entities.DefinitionEntity

data class Definition(
    val zhuyin: String,
    val content: String,
    val sourceDictionary: SourceDictionary,
)

fun DefinitionEntity.asDefinition() = Definition(
    zhuyin = zhuyin,
    content = content,
    sourceDictionary = dictionary,
)
