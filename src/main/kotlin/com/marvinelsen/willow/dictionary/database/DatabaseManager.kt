package com.marvinelsen.willow.dictionary.database

import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.dictionary.SentenceSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.exists

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

    fun createSchema() {
        transaction {
            SchemaUtils.create(EntryTable, DefinitionTable, SentenceTable)
        }
    }

    fun insertEntries(entries: List<Entry>) {
        transaction {
            entries.forEach { entry ->
                val entryEntity = findOrInsertNewEntryEntity(entry)

                entry.definitions.forEach { definition ->
                    DefinitionEntity.new {
                        this.entry = entryEntity
                        this.shortDefinition = definition.shortDefinition
                        this.htmlDefinition = definition.htmlDefinition
                        this.dictionary = definition.sourceDictionary
                    }
                }
            }
        }
    }

    fun insertSentences(sentences: List<Sentence>) {
        transaction {
            sentences.forEach {
                SentenceEntity.new {
                    traditional = it.traditional
                    characterCount = it.traditional.length
                    sentenceSource = SentenceSource.TATOEBA
                }
            }
        }
    }
}

fun findOrInsertNewEntryEntity(entry: Entry) = EntryEntity
    .find { (EntryTable.traditional eq entry.traditional) and (EntryTable.zhuyin eq entry.zhuyin) }
    .firstOrNull()
    ?: entry.toEntity()

private fun Entry.toEntity() = EntryEntity.new {
    traditional = this@toEntity.traditional
    zhuyin = this@toEntity.zhuyin
    accentedPinyin = this@toEntity.accentedPinyin
    numberedPinyin = this@toEntity.numberedPinyin
    characterCount = this@toEntity.traditional.length
}
