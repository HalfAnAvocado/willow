package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.dictionary.objects.Word
import com.marvinelsen.willow.dictionary.objects.asWord
import com.marvinelsen.willow.persistence.entities.WordEntity
import com.marvinelsen.willow.persistence.tables.WordTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction

object Dictionary {
    fun search(query: String) = transaction {
        WordEntity.find { WordTable.traditional like "$query%" }
            .sortedBy { it.characterCount }
            .with(WordEntity::definitions)
            .map { it.asWord() }
    }

    fun findWordsContaining(word: Word) = transaction {
        WordEntity.find { WordTable.traditional like "%${word.traditional}%" }
            .sortedBy { it.characterCount }
            .with(WordEntity::definitions)
            .map { it.asWord() }
    }

    fun findCharactersOf(word: Word) = transaction {
        val characters = word.traditional.split("")
        val characterToOriginalIndexMapping = characters.withIndex().associate { (index, it) -> it to index }

        WordEntity.find { WordTable.traditional inList word.traditional.split("") }
            .with(WordEntity::definitions)
            .map { it.asWord() }
            .sortedBy { characterToOriginalIndexMapping[it.traditional] }
    }
}