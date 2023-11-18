package com.marvinelsen.willow.serialization.lac

import java.io.InputStream
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

object LacParser {
    fun parse(inputStream: InputStream) =
        CSVFormat.DEFAULT
            .parse(inputStream.bufferedReader())
            .drop(1)
            .map(CSVRecord::toLacEntry)
}

private fun CSVRecord.toLacEntry(): LacEntry {
    val headword = this[5]
    val zhuyin = this[10]
    val definitions = (14..43)
        .mapNotNull { this[it] }
        .filterNot { it.isBlank() }
        .map {
            it.replace("～", "<span class=\"headword\">${headword}</span>").split('\n').joinToString(separator = "") { "<li>${it.substringAfter('.')}</li>" }
        }

    return LacEntry(
        headword = headword,
        zhuyin = zhuyin,
        definitions = definitions
    )
}