package com.marvinelsen.willow.persistence.cedict

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object WordTable : IntIdTable() {
    val traditional = text(name = "traditional").uniqueIndex()
    val simplified = text(name = "simplified")
}

class WordEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WordEntity>(WordTable)

    var traditional by WordTable.traditional
    var simplified by WordTable.simplified
    val definitions by DefinitionEntity referrersOn DefinitionTable.word
}