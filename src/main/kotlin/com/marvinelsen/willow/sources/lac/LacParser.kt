package com.marvinelsen.willow.sources.lac

import com.marvinelsen.willow.sources.common.Parser
import com.marvinelsen.willow.util.PronunciationConverter
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStream

private const val TRADITIONAL_COLUMN_INDEX = 5
private const val ZHUYIN_TAIWAN_COLUMN_INDEX = 10
private const val ZHUYIN_MAINLAND_COLUMN_INDEX = 12
private const val DEFINITION_START_COLUMN_INDEX = 14
private const val DEFINITION_END_COLUMN_INDEX = 43
private val DEFINITION_COLUMNS_INDICES = (DEFINITION_START_COLUMN_INDEX..DEFINITION_END_COLUMN_INDEX)

object LacParser : Parser<LacEntry> {
    override fun parse(inputStream: InputStream) =
        CSVFormat.DEFAULT
            .parse(inputStream.bufferedReader())
            .drop(1)
            .map(CSVRecord::toLacEntry)
}

private fun CSVRecord.toLacEntry(): LacEntry {
    val traditional = this[TRADITIONAL_COLUMN_INDEX]

    val zhuyinTaiwan = this[ZHUYIN_TAIWAN_COLUMN_INDEX].replace(
        "丨",
        "ㄧ"
    ).replace("，", PronunciationConverter.ZHUYIN_SEPARATOR)
    val zhuyinMainland = this[ZHUYIN_MAINLAND_COLUMN_INDEX].replace(
        "丨",
        "ㄧ"
    ).replace("，", PronunciationConverter.ZHUYIN_SEPARATOR)

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
