package com.marvinelsen.willow.sources.cedict

import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.dictionary.database.DatabaseManager
import com.marvinelsen.willow.dictionary.database.DefinitionEntity
import com.marvinelsen.willow.sources.common.DatabaseImporter
import com.marvinelsen.willow.util.PronunciationConverter
import org.jetbrains.exposed.sql.transactions.transaction

object CedictDatabaseImporter : DatabaseImporter<CedictEntry> {
    override fun import(entries: List<CedictEntry>) {
        transaction {
            entries.forEach {
                val zhuyin = PronunciationConverter.convertToZhuyin(it.numberedPinyinTaiwan ?: it.numberedPinyin)
                DefinitionEntity.new {
                    entry = DatabaseManager.findOrCreateEntryEntity(
                        traditional = it.traditional,
                        zhuyin = zhuyin,
                        numberedPinyin = it.numberedPinyinTaiwan ?: it.numberedPinyin
                    )
                    shortDefinition = CedictDefinitionFormatter.formatShortDefinition(it)
                    htmlDefinition = CedictDefinitionFormatter.formatHtmlDefinition(it)
                    dictionary = SourceDictionary.CEDICT
                }
            }
        }
    }
}
