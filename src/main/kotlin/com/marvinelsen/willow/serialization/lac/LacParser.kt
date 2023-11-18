package com.marvinelsen.willow.serialization.lac

import java.io.InputStream
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

const val TRADITIONAL_COLUMN_INDEX = 5
const val ZHUYIN_COLUMN_INDEX = 10
val DEFINITION_COLUMNS_INDICES = (14..43)

object LacParser {
    fun parse(inputStream: InputStream) =
        CSVFormat.DEFAULT
            .parse(inputStream.bufferedReader())
            .drop(1)
            .map(CSVRecord::toLacEntry)
}

private fun CSVRecord.toLacEntry(): LacEntry {
    val traditional = this[TRADITIONAL_COLUMN_INDEX]
    val zhuyin = this[ZHUYIN_COLUMN_INDEX].replace("丨", "ㄧ")
    val definitions = DEFINITION_COLUMNS_INDICES
        .mapNotNull { this[it] }
        .filterNot { it.isBlank() }
        .map {
            it.replace("～", "<span class=\"headword\">${traditional}</span>").split('\n').joinToString(separator = "") { "<li>${it.substringAfter('.')}</li>" }
        }

    return LacEntry(
        traditional = traditional,
        zhuyin = zhuyin,
        definitions = definitions
    )
}