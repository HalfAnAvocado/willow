package com.marvinelsen.willow.dictionary.database

import com.marvinelsen.willow.dictionary.SentenceSource
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object SentenceTable : IntIdTable("sentence") {
    val traditional = text("traditional").index("idx_sentence_traditional")
    val characterCount = integer("character_count")
    val sentenceSource = enumeration<SentenceSource>("sentence_source")
}

class SentenceEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SentenceEntity>(SentenceTable)

    var traditional by SentenceTable.traditional
    var characterCount by SentenceTable.characterCount
    var sentenceSource by SentenceTable.sentenceSource
}
