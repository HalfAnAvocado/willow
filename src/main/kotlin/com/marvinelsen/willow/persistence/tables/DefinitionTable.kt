package com.marvinelsen.willow.persistence.tables

import com.marvinelsen.willow.service.objects.Dictionary
import org.jetbrains.exposed.dao.id.IntIdTable

object DefinitionTable : IntIdTable(name = "definitions") {
    val word = reference(name = "word", foreign = WordTable).index()
    val content = text(name = "content")
    val numberedPinyin = text(name = "numbered_pinyin")
    val numberedPinyinTaiwan = text(name = "numbered_pinyin_taiwan").nullable()
    val dictionary = enumeration<Dictionary>(name = "dictionary")
}
