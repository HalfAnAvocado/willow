package com.marvinelsen.willow.sources.lac

import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.dictionary.database.DatabaseManager
import com.marvinelsen.willow.dictionary.database.DefinitionEntity
import com.marvinelsen.willow.sources.common.DatabaseImporter
import org.jetbrains.exposed.sql.transactions.transaction

object LacDatabaseImporter : DatabaseImporter<LacEntry> {
    override fun import(entries: List<LacEntry>) {
        transaction {
            entries.forEach {
                DefinitionEntity.new {
                    entry = DatabaseManager.findOrCreateEntryEntity(
                        it.traditional,
                        it.zhuyinTaiwan.ifBlank { it.zhuyinMainland })
                    shortDefinition = LacDefinitionFormatter.formatShortDefinition(it)
                    htmlDefinition = LacDefinitionFormatter.formatHtmlDefinition(it)
                    dictionary = SourceDictionary.LAC
                }
            }
        }
    }
}