package com.marvinelsen.willow.service.objects

import com.marvinelsen.willow.persistence.entities.DefinitionEntity

data class Definition(
    val numberedPinyin: String,
    val numberedPinyinTaiwan: String? = null,
    val content: String,
    val dictionary: Dictionary,
)

fun DefinitionEntity.asDefinition() = Definition(
    numberedPinyin = numberedPinyin,
    numberedPinyinTaiwan = numberedPinyinTaiwan,
    content = content,
    dictionary = dictionary,
)
