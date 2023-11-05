package com.marvinelsen.willow.serialization.cedict

data class CedictEntry(
    val traditional: String,
    val simplified: String,
    val numberedPinyin: String,
    val numberedPinyinTaiwan: String? = null,
    val definitions: String,
)