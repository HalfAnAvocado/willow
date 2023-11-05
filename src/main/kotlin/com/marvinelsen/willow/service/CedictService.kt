package com.marvinelsen.willow.service

import com.marvinelsen.willow.persistence.cedict.WordEntity
import com.marvinelsen.willow.persistence.cedict.WordTable
import com.marvinelsen.willow.service.objects.asWord
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction

object CedictService {
    fun search(query: String) = transaction {
        WordEntity.find { WordTable.traditional like "$query%" }
            .with(WordEntity::definitions)
            .map { it.asWord() }
    }
}