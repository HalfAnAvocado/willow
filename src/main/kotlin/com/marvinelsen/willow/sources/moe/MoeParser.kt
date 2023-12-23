package com.marvinelsen.willow.sources.moe

import com.marvinelsen.willow.sources.common.Parser
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

@OptIn(ExperimentalSerializationApi::class)
object MoeParser : Parser<MoeEntry> {
    override fun parse(inputStream: InputStream) = Json.decodeFromStream<List<MoeEntry>>(inputStream)
}
