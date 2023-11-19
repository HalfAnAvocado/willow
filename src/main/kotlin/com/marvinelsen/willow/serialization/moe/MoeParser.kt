package com.marvinelsen.willow.serialization.moe

import com.marvinelsen.willow.serialization.common.Parser
import java.io.InputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
object MoeParser : Parser<MoeEntry> {
    override fun parse(inputStream: InputStream) = Json.decodeFromStream<List<MoeEntry>>(inputStream)
}