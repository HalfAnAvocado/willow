package com.marvinelsen.willow.persistence.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WordTable : IntIdTable(name = "words") {
    val traditional = text(name = "traditional").uniqueIndex("idx_words_traditional")
    val simplified = text(name = "simplified").nullable()
    val characterCount = integer(name = "character_count")
}
