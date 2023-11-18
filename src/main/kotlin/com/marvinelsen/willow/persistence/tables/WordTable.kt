package com.marvinelsen.willow.persistence.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WordTable : IntIdTable("word") {
    val traditional = text("traditional").index("idx_word_traditional")
    val zhuyin = text("zhuyin").index("idx_word_zhuyin")
    val characterCount = integer("character_count")
}