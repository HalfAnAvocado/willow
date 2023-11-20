package com.marvinelsen.willow.persistence.entities

import com.marvinelsen.willow.persistence.tables.DefinitionTable
import com.marvinelsen.willow.persistence.tables.EntryTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EntryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntryEntity>(EntryTable)

    var traditional by EntryTable.traditional
    var zhuyin by EntryTable.zhuyin
    var characterCount by EntryTable.characterCount

    val definitions by DefinitionEntity referrersOn DefinitionTable.entry
}