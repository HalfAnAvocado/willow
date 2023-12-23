package com.marvinelsen.willow.anki.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val deckName: String,
    val modelName: String,
    @SerialName("fields")
    val fieldsContents: Map<String, String>,
    val tags: List<String> = emptyList(),
)
