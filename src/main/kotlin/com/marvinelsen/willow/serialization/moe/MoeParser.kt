package com.marvinelsen.willow.serialization.moe

import java.io.InputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
object MoeParser {
    fun parse(inputSteam: InputStream) = Json.decodeFromStream<List<MoeEntry>>(inputSteam)
}