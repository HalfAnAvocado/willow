package com.marvinelsen.willow.persistence

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.objects.SourceDictionary
import com.marvinelsen.willow.persistence.entities.DefinitionEntity
import com.marvinelsen.willow.persistence.entities.WordEntity
import com.marvinelsen.willow.persistence.tables.DefinitionTable
import com.marvinelsen.willow.persistence.tables.WordTable
import com.marvinelsen.willow.serialization.cedict.CedictDefinitionFormatter
import com.marvinelsen.willow.serialization.cedict.CedictParser
import com.marvinelsen.willow.serialization.lac.LacParser
import com.marvinelsen.willow.serialization.moe.MoeDefinitionFormatter
import com.marvinelsen.willow.serialization.moe.MoeParser
import com.marvinelsen.willow.util.PronunciationConverter
import java.sql.Connection
import java.util.zip.GZIPInputStream
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseManager {
    fun init() {
        Database.connect("jdbc:sqlite:data.db?case_sensitive_like=ON&foreign_keys=ON", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    @Suppress("Unused")
    fun createDatabase() {
        transaction {
            SchemaUtils.create(WordTable, DefinitionTable)
        }

        importCedict()
        importMoe()
        importLac()
    }

    private fun importCedict() {
        val cedictEntries =
            CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/cedict_1_0_ts_utf-8_mdbg.txt.gz")))

        transaction {
            cedictEntries.forEach {
                val zhuyin = PronunciationConverter.convertToZhuyin(it.numberedPinyinTaiwan ?: it.numberedPinyin)
                DefinitionEntity.new {
                    word = findOrCreateWordEntity(it.traditional, zhuyin)
                    shortDefinition = CedictDefinitionFormatter.formatShortDefinition(it)
                    htmlDefinition = CedictDefinitionFormatter.formatHtmlDefinition(it)
                    dictionary = SourceDictionary.CEDICT
                }
            }
        }
    }

    private fun importMoe() {
        val moeEntries =
            MoeParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/moedict.json.gz")))

        transaction {
            moeEntries.filter { !it.title.startsWith("{") }.forEach { entry ->
                entry.heteronyms.filter { it.zhuyin != null }.forEach { heteronym ->
                    val zhuyin = heteronym.zhuyin!!

                    DefinitionEntity.new {
                        word = findOrCreateWordEntity(entry.title, zhuyin)
                        shortDefinition = MoeDefinitionFormatter.formatShortDefinition(heteronym.definitions.first())
                        htmlDefinition = MoeDefinitionFormatter.formatHtmlDefinition(heteronym.definitions)
                        dictionary = SourceDictionary.MOE
                    }
                }
            }
        }
    }

    private fun importLac() {
        val lacEntries =
            LacParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/lac.csv.gz")))

        transaction {
            lacEntries.forEach {
                DefinitionEntity.new {
                    word = findOrCreateWordEntity(it.traditional, it.zhuyinTaiwan.ifBlank { it.zhuyinMainland })
                    shortDefinition = it.definitions.joinToString(separator = "")
                    htmlDefinition = it.definitions.joinToString(separator = "")
                    dictionary = SourceDictionary.LAC
                }
            }
        }
    }

    private fun findOrCreateWordEntity(traditional: String, zhuyin: String) =
        WordEntity.find { (WordTable.traditional eq traditional) and (WordTable.zhuyin eq zhuyin) }.firstOrNull()
            ?: WordEntity.new {
                this.traditional = traditional
                this.zhuyin = zhuyin
                this.characterCount = traditional.length
            }
}