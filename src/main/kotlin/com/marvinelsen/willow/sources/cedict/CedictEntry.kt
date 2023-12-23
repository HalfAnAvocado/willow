package com.marvinelsen.willow.sources.cedict

data class CedictEntry(
    val traditional: String,
    val simplified: String,
    val numberedPinyin: String,
    val numberedPinyinTaiwan: String? = null,
    val definition: String,
)
