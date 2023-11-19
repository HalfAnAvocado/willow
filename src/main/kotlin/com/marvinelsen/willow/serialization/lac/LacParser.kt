package com.marvinelsen.willow.serialization.lac

import com.marvinelsen.willow.serialization.common.Parser
import java.io.InputStream
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

private const val TRADITIONAL_COLUMN_INDEX = 5
private const val ZHUYIN_TAIWAN_COLUMN_INDEX = 10
private const val ZHUYIN_MAINLAND_COLUMN_INDEX = 12
private val DEFINITION_COLUMNS_INDICES = (14..43)

object LacParser : Parser<LacEntry> {
    override fun parse(inputStream: InputStream) =
        CSVFormat.DEFAULT
            .parse(inputStream.bufferedReader())
            .drop(1)
            .map(CSVRecord::toLacEntry)
}

private fun CSVRecord.toLacEntry(): LacEntry {
    val traditional = this[TRADITIONAL_COLUMN_INDEX]

    val zhuyinTaiwan = this[ZHUYIN_TAIWAN_COLUMN_INDEX].replace("丨", "ㄧ")
    val zhuyinMainland = this[ZHUYIN_MAINLAND_COLUMN_INDEX].replace("丨", "ㄧ")

    val definitions = DEFINITION_COLUMNS_INDICES
        .mapNotNull { this[it] }
        .filterNot { it.isBlank() }

    return LacEntry(
        traditional = traditional,
        zhuyinTaiwan = zhuyinTaiwan,
        zhuyinMainland = zhuyinMainland,
        definitions = definitions
    )
}