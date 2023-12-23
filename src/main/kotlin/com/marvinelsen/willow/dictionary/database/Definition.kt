package com.marvinelsen.willow.dictionary.database

import com.marvinelsen.willow.dictionary.SourceDictionary
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object DefinitionTable : IntIdTable("definition") {
    val entry =
        reference(name = "entry", foreign = EntryTable, fkName = "fk_definition_entry").index("idx_definition_entry")
    val shortDefinition = text("short_definition")
    val htmlDefinition = text("html_definition")
    val sourceDictionary = enumeration<SourceDictionary>("source_dictionary")
}

class DefinitionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DefinitionEntity>(DefinitionTable)

    var entry by EntryEntity referencedOn DefinitionTable.entry
    var shortDefinition by DefinitionTable.shortDefinition
    var htmlDefinition by DefinitionTable.htmlDefinition
    var dictionary by DefinitionTable.sourceDictionary
}
