package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.dictionary.database.DatabaseManager
import com.marvinelsen.willow.dictionary.database.DefinitionEntity
import com.marvinelsen.willow.dictionary.database.EntryEntity
import com.marvinelsen.willow.dictionary.database.EntryTable
import com.marvinelsen.willow.dictionary.database.SentenceEntity
import com.marvinelsen.willow.dictionary.database.SentenceTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object Dictionary {
    fun search(query: String) = transaction {
        EntryEntity.find { EntryTable.traditional like "$query%" }
            .sortedBy { it.characterCount }
            .with(EntryEntity::definitions)
            .map { it.toEntry() }
    }

    fun findEntriesContaining(entry: Entry) = transaction {
        EntryEntity.find { EntryTable.traditional like "_%${entry.traditional}%" }
            .sortedBy { it.characterCount }
            .with(EntryEntity::definitions)
            .map { it.toEntry() }
    }

    fun findCharactersOf(entry: Entry): List<Entry> {
        val characterToEntryMap = transaction {
            EntryEntity
                .find {
                    (EntryTable.traditional inList entry.characters) and
                        (EntryTable.zhuyin inList entry.zhuyinSyllables)
                }
                .with(EntryEntity::definitions)
                .map { it.toEntry() }
                .associateBy { it.traditional }
        }
        return entry.characters.mapNotNull { characterToEntryMap[it] }
    }

    fun findSentencesFor(entry: Entry) = transaction {
        SentenceEntity.find { SentenceTable.traditional like "%${entry.traditional}%" }
            .sortedWith(compareBy({ it.sentenceSource }, { it.characterCount }))
            .map { it.toSentence() }
    }

    fun addUserSentence(sentence: Sentence) = transaction {
        SentenceEntity.new {
            traditional = sentence.traditional
            characterCount = sentence.traditional.length
            sentenceSource = SentenceSource.USER
        }
    }

    fun addUserEntry(userEntry: Entry) = transaction {
        val entryEntity = DatabaseManager.findOrCreateEntryEntity(userEntry.traditional, userEntry.zhuyin)
        val definition = userEntry.definitions[SourceDictionary.USER]!!.first()

        DefinitionEntity.new {
            entry = entryEntity
            shortDefinition = definition.shortDefinition
            htmlDefinition = definition.htmlDefinition
            dictionary = SourceDictionary.USER
        }
    }
}

private fun EntryEntity.toEntry() = Entry(
    traditional = traditional,
    zhuyin = zhuyin,
    definitions = definitions.map { it.toDefinition() }.groupBy { it.sourceDictionary }
)

private fun DefinitionEntity.toDefinition() = Definition(
    shortDefinition = shortDefinition,
    htmlDefinition = htmlDefinition,
    sourceDictionary = dictionary,
)

private fun SentenceEntity.toSentence() = Sentence(traditional = traditional)
