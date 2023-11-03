package com.marvinelsen.willow.persistence.common

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

open class RowIdTable(name: String = "", columnName: String = "id") : IdTable<Int>(name) {
    final override val id: Column<EntityID<Int>> = integer(columnName).entityId()
    final override val primaryKey = PrimaryKey(id)
}