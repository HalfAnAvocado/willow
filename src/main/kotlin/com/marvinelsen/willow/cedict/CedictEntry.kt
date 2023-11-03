package com.marvinelsen.willow.cedict

import com.marvinelsen.willow.persistence.cedict.CedictEntity

data class CedictEntry(
    val traditional: String,
    val simplified: String,
    val numberedPinyin: String,
    val definitions: List<String>,
)

fun CedictEntry.toEntity() = CedictEntity.new {
    traditional = this@toEntity.traditional
    simplified = this@toEntity.simplified
    numberedPinyin = this@toEntity.numberedPinyin
    definitions = this@toEntity.definitions.joinToString(separator = "/")
}
