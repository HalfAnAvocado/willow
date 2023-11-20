package com.marvinelsen.willow.dictionary

import com.huaban.analysis.jieba.JiebaSegmenter
import com.marvinelsen.willow.dictionary.objects.Definition
import com.marvinelsen.willow.dictionary.objects.Entry
import com.marvinelsen.willow.dictionary.database.entities.DefinitionEntity
import com.marvinelsen.willow.dictionary.database.entities.EntryEntity
import com.marvinelsen.willow.dictionary.database.tables.EntryTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object Dictionary {
    private val segmenter = JiebaSegmenter()
    fun search(query: String) = transaction {
        val segments = segmenter.process(query, JiebaSegmenter.SegMode.SEARCH)

        if (segments.size == 1) {
            EntryEntity.find { EntryTable.traditional like "$query%" }
                .sortedBy { it.characterCount }
                .with(EntryEntity::definitions)
                .map { it.asEntry() }
        } else {
            EntryEntity.find { EntryTable.traditional inList segments.map { it.word.token } }
                .with(EntryEntity::definitions)
                .map { it.asEntry() }
        }
    }

    fun findEntriesContaining(entry: Entry) = transaction {
        EntryEntity.find { EntryTable.traditional like "%${entry.traditional}%" }
            .sortedBy { it.characterCount }
            .with(EntryEntity::definitions)
            .map { it.asEntry() }
    }

    fun findCharactersOf(entry: Entry): List<Entry> {
        val characterToEntryMap = transaction {
            EntryEntity.find { (EntryTable.traditional inList entry.characters) and (EntryTable.zhuyin inList entry.zhuyinSyllables) }
                .with(EntryEntity::definitions)
                .map { it.asEntry() }
                .associateBy { it.traditional }
        }
        return entry.characters.mapNotNull { characterToEntryMap[it] }
    }
}

private fun EntryEntity.asEntry() = Entry(
    traditional = traditional,
    zhuyin = zhuyin,
    definitions = definitions.map { it.asDefinition() }.groupBy { it.sourceDictionary })

private fun DefinitionEntity.asDefinition() = Definition(
    shortDefinition = shortDefinition,
    htmlDefinition = htmlDefinition,
    sourceDictionary = dictionary,
)