package com.marvinelsen.willow.persistence.entities

import com.marvinelsen.willow.persistence.tables.DefinitionTable
import com.marvinelsen.willow.persistence.tables.WordTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WordEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WordEntity>(WordTable)

    var traditional by WordTable.traditional
    var zhuyin by WordTable.zhuyin
    var characterCount by WordTable.characterCount

    val definitions by DefinitionEntity referrersOn DefinitionTable.word
}