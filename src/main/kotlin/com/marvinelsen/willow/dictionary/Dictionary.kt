package com.marvinelsen.willow.dictionary

import com.huaban.analysis.jieba.JiebaSegmenter
import com.marvinelsen.willow.dictionary.database.DefinitionEntity
import com.marvinelsen.willow.dictionary.database.EntryEntity
import com.marvinelsen.willow.dictionary.database.EntryTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object Dictionary {
    private val segmenter = JiebaSegmenter()
    fun search(query: String) = transaction {
/*
        val segments = segmenter.process(query, JiebaSegmenter.SegMode.SEARCH)

        if (segments.size == 1) {
*/
            EntryEntity.find { EntryTable.traditional like "$query%" }
                .sortedBy { it.characterCount }
                .with(EntryEntity::definitions)
                .map { it.toEntry() }
/*
        } else {
            EntryEntity.find { EntryTable.traditional inList segments.map { it.word.token } }
                .with(EntryEntity::definitions)
                .map { it.toEntry() }
        }
*/
    }

    fun findEntriesContaining(entry: Entry) = transaction {
        EntryEntity.find { EntryTable.traditional like "_%${entry.traditional}%" }
            .sortedBy { it.characterCount }
            .with(EntryEntity::definitions)
            .map { it.toEntry() }
    }

    fun findCharactersOf(entry: Entry): List<Entry> {
        val characterToEntryMap = transaction {
            EntryEntity.find { (EntryTable.traditional inList entry.characters) and (EntryTable.zhuyin inList entry.zhuyinSyllables) }
                .with(EntryEntity::definitions)
                .map { it.toEntry() }
                .associateBy { it.traditional }
        }
        return entry.characters.mapNotNull { characterToEntryMap[it] }
    }
}

private fun EntryEntity.toEntry() = Entry(
    traditional = traditional,
    zhuyin = zhuyin,
    definitions = definitions.map { it.toDefinition() }.groupBy { it.sourceDictionary })

private fun DefinitionEntity.toDefinition() = Definition(
    shortDefinition = shortDefinition,
    htmlDefinition = htmlDefinition,
    sourceDictionary = dictionary,
)