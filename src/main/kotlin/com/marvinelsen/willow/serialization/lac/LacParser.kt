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

private fun CSVRecord.toLacEntry(): LacEntry =
    LacEntry(
        headword = this[5],
        zhuyin = this[10],
        definitions = (14..43).mapNotNull { this[it] }
    )
