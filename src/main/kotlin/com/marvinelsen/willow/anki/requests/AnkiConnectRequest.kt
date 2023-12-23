@file:OptIn(ExperimentalSerializationApi::class)

package com.marvinelsen.willow.anki.requests

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

private const val ANKI_CONNECT_VERSION = 6

@Serializable
sealed class AnkiConnectRequest<T> {
    abstract val action: String
    abstract val params: T

    @EncodeDefault
    val version: Int = ANKI_CONNECT_VERSION
}

@Serializable
sealed class Params
