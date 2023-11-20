package com.marvinelsen.willow.dictionary

import com.huaban.analysis.jieba.JiebaSegmenter
import com.marvinelsen.willow.dictionary.objects.Definition
import com.marvinelsen.willow.dictionary.objects.Entry
import com.marvinelsen.willow.persistence.entities.DefinitionEntity
import com.marvinelsen.willow.persistence.entities.EntryEntity
import com.marvinelsen.willow.persistence.tables.EntryTable
import org.jetbrains.exposed.dao.with
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

    fun findCharactersOf(entry: Entry) = transaction {
        val characters = entry.traditional.split("")
        val characterToOriginalIndexMapping = characters.withIndex().associate { (index, it) -> it to index }

        EntryEntity.find { EntryTable.traditional inList entry.traditional.split("") }
            .with(EntryEntity::definitions)
            .map { it.asEntry() }
            .sortedBy { characterToOriginalIndexMapping[it.traditional] }
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