@file:OptIn(ExperimentalSerializationApi::class)

package com.marvinelsen.willow.anki.requests

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
sealed class AnkiConnectRequest<T>() {
    abstract val action: String
    abstract val params: T

    @EncodeDefault
    val version: Int = 6
}

@Serializable
sealed class Params