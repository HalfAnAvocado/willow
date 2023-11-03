package com.marvinelsen.willow.cedict

data class CedictEntry(
    val traditional: String,
    val simplified: String,
    val numberedPinyin: String,
    val definitions: List<String>,
)