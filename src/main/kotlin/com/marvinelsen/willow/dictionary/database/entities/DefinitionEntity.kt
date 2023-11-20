package com.marvinelsen.willow.dictionary.database.entities

import com.marvinelsen.willow.dictionary.database.tables.DefinitionTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DefinitionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DefinitionEntity>(DefinitionTable)

    var entry by EntryEntity referencedOn DefinitionTable.entry
    var shortDefinition by DefinitionTable.shortDefinition
    var htmlDefinition by DefinitionTable.htmlDefinition
    var dictionary by DefinitionTable.sourceDictionary
}
