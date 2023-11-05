package com.marvinelsen.willow.persistence.cedict

import com.marvinelsen.willow.service.objects.Dictionary
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object DefinitionTable : IntIdTable() {
    val word = reference(name = "word", foreign = WordTable)
    val content = text(name = "content")
    val numberedPinyin = text(name = "numbered_pinyin")
    val numberedPinyinTaiwan = text(name = "numbered_pinyin_taiwan").nullable()
    val dictionary = enumeration<Dictionary>(name = "dictionary")
}

class DefinitionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DefinitionEntity>(DefinitionTable)

    var word by WordEntity referencedOn DefinitionTable.word
    var numberedPinyin by DefinitionTable.numberedPinyin
    var numberedPinyinTaiwan by DefinitionTable.numberedPinyinTaiwan
    var content by DefinitionTable.content
    var dictionary by DefinitionTable.dictionary
}
