package com.marvinelsen.willow.persistence.cedict

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object CedictTable : IntIdTable() {
    val traditional = text(name = "traditional").index()
    val simplified = text(name = "simplified")
    val numberedPinyin = text(name = "numbered_pinyin")
    val numberedPinyinTaiwan = text(name = "numbered_pinyin_taiwan").nullable()
    val definitions = text(name = "definitions")
}

class CedictEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CedictEntity>(CedictTable)

    var traditional by CedictTable.traditional
    var simplified by CedictTable.simplified
    var numberedPinyin by CedictTable.numberedPinyin
    var numberedPinyinTaiwan by CedictTable.numberedPinyinTaiwan
    var definitions: List<String> by CedictTable.definitions.transform(
        toColumn = { it.joinToString(separator = "/") },
        toReal = { it.split("/") })
}