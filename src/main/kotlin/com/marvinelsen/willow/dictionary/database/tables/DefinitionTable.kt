package com.marvinelsen.willow.dictionary.database.tables

import com.marvinelsen.willow.dictionary.objects.SourceDictionary
import org.jetbrains.exposed.dao.id.IntIdTable

object DefinitionTable : IntIdTable("definition") {
    val entry =
        reference(name = "entry", foreign = EntryTable, fkName = "fk_definition_entry").index("idx_definition_entry")
    val shortDefinition = text("short_definition")
    val htmlDefinition = text("html_definition")
    val sourceDictionary = enumeration<SourceDictionary>("source_dictionary")
}