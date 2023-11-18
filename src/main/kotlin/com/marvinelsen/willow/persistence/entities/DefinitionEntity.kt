package com.marvinelsen.willow.persistence.entities

import com.marvinelsen.willow.persistence.tables.DefinitionTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DefinitionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DefinitionEntity>(DefinitionTable)

    var word by WordEntity referencedOn DefinitionTable.word
    var shortDefinition by DefinitionTable.shortDefinition
    var htmlDefinition by DefinitionTable.htmlDefinition
    var dictionary by DefinitionTable.sourceDictionary
}
