package com.marvinelsen.willow.persistence.tables

import com.marvinelsen.willow.dictionary.objects.SourceDictionary
import org.jetbrains.exposed.dao.id.IntIdTable

object DefinitionTable : IntIdTable("definition") {
    val word = reference(name = "word", foreign = WordTable, fkName = "fk_definition_word").index("idx_definition_word")
    val shortDefinition = text("short_definition")
    val htmlDefinition = text("html_definition")
    val sourceDictionary = enumeration<SourceDictionary>("dictionary")
}