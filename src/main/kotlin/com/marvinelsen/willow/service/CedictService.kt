package com.marvinelsen.willow.service

import com.marvinelsen.willow.persistence.entities.WordEntity
import com.marvinelsen.willow.persistence.tables.WordTable
import com.marvinelsen.willow.service.objects.Word
import com.marvinelsen.willow.service.objects.asWord
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction

object CedictService {
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
        WordEntity.find { WordTable.traditional inList word.traditional.split("") }
            .with(WordEntity::definitions)
            .map { it.asWord() }
    }
}