package com.marvinelsen.willow.persistence.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WordTable : IntIdTable(name = "words") {
    val traditional = text(name = "traditional").index("ix_words_traditional")
    val zhuyin = text(name = "zhuyin").index("ix_words_zhuyin")
    val characterCount = integer(name = "character_count")
}
