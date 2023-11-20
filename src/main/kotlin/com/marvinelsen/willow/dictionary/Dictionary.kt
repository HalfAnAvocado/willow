package com.marvinelsen.willow.dictionary

import com.huaban.analysis.jieba.JiebaSegmenter
import com.marvinelsen.willow.dictionary.objects.Definition
import com.marvinelsen.willow.dictionary.objects.Word
import com.marvinelsen.willow.persistence.entities.DefinitionEntity
import com.marvinelsen.willow.persistence.entities.WordEntity
import com.marvinelsen.willow.persistence.tables.WordTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction

object Dictionary {
    private val segmenter = JiebaSegmenter()
    fun search(query: String) = transaction {
        val segments = segmenter.process(query, JiebaSegmenter.SegMode.SEARCH)

        if (segments.size == 1) {
            WordEntity.find { WordTable.traditional like "$query%" }
                .sortedBy { it.characterCount }
                .with(WordEntity::definitions)
                .map { it.asWord() }
        } else {
            WordEntity.find { WordTable.traditional inList segments.map { it.word.token } }
                .with(WordEntity::definitions)
                .map { it.asWord() }
        }
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

private fun WordEntity.asWord() = Word(
    traditional = traditional,
    zhuyin = zhuyin,
    definitions = definitions.map { it.asDefinition() }.groupBy { it.sourceDictionary })

private fun DefinitionEntity.asDefinition() = Definition(
    shortDefinition = shortDefinition,
    htmlDefinition = htmlDefinition,
    sourceDictionary = dictionary,
)