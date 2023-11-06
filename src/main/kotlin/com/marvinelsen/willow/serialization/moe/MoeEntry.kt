package com.marvinelsen.willow.serialization.moe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoeEntry(
    val title: String,
    val heteronyms: List<MoeHeteronym>,
    @SerialName("stroke_count") val strokeCount: Int? = null,
    @SerialName("non_radical_stroke_count") val nonRadicalStrokeCount: Int? = null,
    val radical: String? = null,
)

@Serializable
data class MoeHeteronym(
    @SerialName("bopomofo") val zhuyin: String? = null,
    @SerialName("pinyin") val accentedPinyin: String? = null,
    val definitions: List<MoeDefinition>,
)

@Serializable
data class MoeDefinition(
    @SerialName("def") val content: String,
    @SerialName("example") val examples: List<String> = emptyList(),
    @SerialName("quote") val quotes: List<String> = emptyList(),
    val type: String? = null,
    @SerialName("link") val links: List<String> = emptyList(),
    val synonyms: String? = null,
    val antonyms: String? = null,
)
