package com.marvinelsen.willow.dictionary.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object EntryTable : IntIdTable("entry") {
    val traditional = text("traditional").index("idx_entry_traditional")
    val zhuyin = text("zhuyin").index("idx_entry_zhuyin")
    val characterCount = integer("character_count")
}