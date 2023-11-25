package com.marvinelsen.willow.dictionary.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object EntryTable : IntIdTable("entry") {
    val traditional = text("traditional").index("idx_entry_traditional")
    val zhuyin = text("zhuyin").index("idx_entry_zhuyin")
    val characterCount = integer("character_count")
}

class EntryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntryEntity>(EntryTable)

    var traditional by EntryTable.traditional
    var zhuyin by EntryTable.zhuyin
    var characterCount by EntryTable.characterCount

    val definitions by DefinitionEntity referrersOn DefinitionTable.entry
}