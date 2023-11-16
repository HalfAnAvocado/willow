package com.marvinelsen.willow.dictionary.objects

import com.marvinelsen.willow.persistence.entities.DefinitionEntity

data class Definition(
    val numberedPinyin: String,
    val numberedPinyinTaiwan: String? = null,
    val content: String,
    val sourceDictionary: SourceDictionary,
)

fun DefinitionEntity.asDefinition() = Definition(
    numberedPinyin = numberedPinyin,
    numberedPinyinTaiwan = numberedPinyinTaiwan,
    content = content,
    sourceDictionary = dictionary,
)
