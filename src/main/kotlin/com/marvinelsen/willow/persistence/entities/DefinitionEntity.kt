package com.marvinelsen.willow.persistence.entities

import com.marvinelsen.willow.persistence.tables.DefinitionTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DefinitionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DefinitionEntity>(DefinitionTable)

    var word by WordEntity referencedOn DefinitionTable.word
    var numberedPinyin by DefinitionTable.numberedPinyin
    var numberedPinyinTaiwan by DefinitionTable.numberedPinyinTaiwan
    var content by DefinitionTable.content
    var dictionary by DefinitionTable.dictionary
}
