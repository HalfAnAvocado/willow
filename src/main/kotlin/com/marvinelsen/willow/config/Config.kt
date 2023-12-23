package com.marvinelsen.willow.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(val anki: AnkiConfig)

@Serializable
data class AnkiConfig(
    val ankiConnectUrl: String,
    val deckName: String,
    val modelName: String,
    val fields: FieldMapping,
)

@Serializable
data class FieldMapping(
    val traditional: String,
    val zhuyin: String,
    val definition: String,
    val exampleSentence: String,
)
