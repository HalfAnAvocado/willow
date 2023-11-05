package com.marvinelsen.willow.service

import com.marvinelsen.willow.persistence.cedict.CedictEntity
import com.marvinelsen.willow.persistence.cedict.CedictTable
import org.jetbrains.exposed.sql.transactions.transaction

object CedictService {
    fun search(query: String) = transaction {
        CedictEntity.find { CedictTable.traditional like "$query%" }.toList()
    }
}