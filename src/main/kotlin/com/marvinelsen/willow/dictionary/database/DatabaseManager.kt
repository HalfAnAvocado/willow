package com.marvinelsen.willow.dictionary.database

import com.marvinelsen.willow.dictionary.database.entities.EntryEntity
import com.marvinelsen.willow.dictionary.database.tables.DefinitionTable
import com.marvinelsen.willow.dictionary.database.tables.EntryTable
import com.marvinelsen.willow.sources.cedict.CedictDatabaseImporter
import com.marvinelsen.willow.sources.cedict.CedictEntry
import com.marvinelsen.willow.sources.lac.LacDatabaseImporter
import com.marvinelsen.willow.sources.lac.LacEntry
import com.marvinelsen.willow.sources.moe.MoeDatabaseImporter
import com.marvinelsen.willow.sources.moe.MoeEntry
import java.sql.Connection
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseManager {
    private val homeDirectory = Path(System.getProperty("user.home"))
    private val dataDirectory =
        Path(System.getenv().getOrDefault("XDG_DATA_HOME", homeDirectory.resolve(".local/share").toString()))
    private val databaseFile = dataDirectory / "willow/dictionary.db"

    fun init() {
        databaseFile.createParentDirectories()
        Database.connect(
            "jdbc:sqlite:${databaseFile.absolutePathString()}?case_sensitive_like=ON&foreign_keys=ON",
            "org.sqlite.JDBC"
        )
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun doesDatabaseExist() = databaseFile.exists()

    fun createDatabase(
        cedictEntries: List<CedictEntry>,
        moeEntries: List<MoeEntry>,
        lacEntries: List<LacEntry>,
    ) {
        transaction {
            SchemaUtils.create(EntryTable, DefinitionTable)
        }

        CedictDatabaseImporter.import(cedictEntries)
        MoeDatabaseImporter.import(moeEntries)
        LacDatabaseImporter.import(lacEntries)
    }

    fun findOrCreateEntryEntity(traditional: String, zhuyin: String) =
        EntryEntity.find { (EntryTable.traditional eq traditional) and (EntryTable.zhuyin eq zhuyin) }.firstOrNull()
            ?: EntryEntity.new {
                this.traditional = traditional
                this.zhuyin = zhuyin
                this.characterCount = traditional.length
            }
}