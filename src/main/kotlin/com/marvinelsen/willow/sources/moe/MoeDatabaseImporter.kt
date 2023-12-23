package com.marvinelsen.willow.sources.moe

import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.dictionary.database.DatabaseManager
import com.marvinelsen.willow.dictionary.database.DefinitionEntity
import com.marvinelsen.willow.sources.common.DatabaseImporter
import org.jetbrains.exposed.sql.transactions.transaction

object MoeDatabaseImporter : DatabaseImporter<MoeEntry> {
    override fun import(entries: List<MoeEntry>) {
        transaction {
            entries.filter { !it.title.startsWith("{") }.forEach { entry ->
                entry.heteronyms.filter { it.zhuyin != null }.forEach { heteronym ->
                    val zhuyin = heteronym.zhuyin!!.replace("""（.*）""".toRegex(), "")

                    DefinitionEntity.new {
                        this.entry = DatabaseManager.findOrCreateEntryEntity(entry.title, zhuyin)
                        shortDefinition = MoeDefinitionFormatter.formatShortDefinition(heteronym.definitions.first())
                        htmlDefinition = MoeDefinitionFormatter.formatHtmlDefinition(heteronym.definitions)
                        dictionary = SourceDictionary.MOE
                    }
                }
            }
        }
    }
}
