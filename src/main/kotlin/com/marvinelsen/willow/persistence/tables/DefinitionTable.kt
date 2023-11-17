package com.marvinelsen.willow.persistence.tables

import com.marvinelsen.willow.dictionary.objects.SourceDictionary
import org.jetbrains.exposed.dao.id.IntIdTable

object DefinitionTable : IntIdTable(name = "definitions") {
    val word = reference(name = "word", foreign = WordTable).index()
    val content = text(name = "content")
    val zhuyin = text(name = "zhuyin")
    val sourceDictionary = enumeration<SourceDictionary>(name = "dictionary")
}
