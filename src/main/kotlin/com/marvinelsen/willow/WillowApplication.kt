package com.marvinelsen.willow

import com.marvinelsen.willow.dictionary.objects.SourceDictionary
import com.marvinelsen.willow.persistence.entities.DefinitionEntity
import com.marvinelsen.willow.persistence.entities.WordEntity
import com.marvinelsen.willow.persistence.tables.DefinitionTable
import com.marvinelsen.willow.persistence.tables.WordTable
import com.marvinelsen.willow.serialization.cedict.CedictParser
import com.marvinelsen.willow.serialization.lac.LacParser
import com.marvinelsen.willow.serialization.moe.MoeParser
import com.marvinelsen.willow.util.PronunciationConverter
import java.sql.Connection
import java.util.zip.GZIPInputStream
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.text.Font
import javafx.stage.Stage
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class WillowApplication : Application() {
    private val twKaiFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/tw-kai.ttf")!!.toExternalForm(), 12.0)
    private val notoSansTcRegularFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/NotoSansCJKtc-Regular.otf")!!.toExternalForm(), 12.0)
    private val notoSansTcBoldFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/NotoSansCJKtc-Bold.otf")!!.toExternalForm(), 12.0)

    init {
        Database.connect("jdbc:sqlite:data.db?case_sensitive_like=ON", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        // createDatabaseTables()
    }

    override fun start(stage: Stage) {
        stage.title = WINDOW_TITLE
        stage.minWidth = WINDOW_MIN_WIDTH
        stage.minHeight = WINDOW_MIN_HEIGHT

        val fxmlLoader = FXMLLoader(WillowApplication::class.java.getResource("views/main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 600.0, 400.0)
        scene.stylesheets.add(WillowApplication::class.java.getResource("stylesheets/main.css")!!.toExternalForm());
        stage.scene = scene

        stage.show()
    }

    companion object {
        private const val WINDOW_TITLE = "Willow"
        private const val WINDOW_MIN_HEIGHT = 480.0
        private const val WINDOW_MIN_WIDTH = 640.0

        fun main() {
            launch(WillowApplication::class.java)
        }
    }

}


fun createDatabaseTables() {
    val cedictEntries =
        CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/cedict_1_0_ts_utf-8_mdbg.txt.gz")))

    transaction {
        SchemaUtils.create(WordTable, DefinitionTable)

        cedictEntries.groupBy { it.traditional }.values.forEach {
            val wordEntity = WordEntity.new {
                traditional = it.first().traditional
                simplified = it.first().simplified
                characterCount = it.first().traditional.length
            }

            for (cedictEntry in it) {
                DefinitionEntity.new {
                    word = wordEntity
                    zhuyin = PronunciationConverter.convertToZhuyin(
                        cedictEntry.numberedPinyinTaiwan ?: cedictEntry.numberedPinyin
                    )
                    content = cedictEntry.definitions
                    dictionary = SourceDictionary.CEDICT
                }
            }
        }
    }

    val moeEntries =
        MoeParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/moedict.json.gz")))

    transaction {
        moeEntries.filter { !it.title.startsWith("{") }.forEach { entry ->
            val wordEntity = WordEntity.find { WordTable.traditional eq entry.title }.firstOrNull() ?: WordEntity.new {
                traditional = entry.title
                characterCount = entry.title.length
            }

            entry.heteronyms.filter { it.zhuyin != null }.forEach { heteronym ->
                val zhuyin = heteronym.zhuyin

                heteronym.definitions.forEach { definition ->
                    val definitionContent = listOfNotNull(
                        definition.content + definition.examples.joinToString(separator = ""),
                        definition.quotes.joinToString(
                            prefix = "<quote>",
                            separator = "</quote><quote>",
                            postfix = "</quote>"
                        ),
                        "似：${definition.synonyms?.replace(",", "、")}".takeIf { definition.synonyms != null },
                        "反：${definition.antonyms?.replace(",", "、")}".takeIf { definition.antonyms != null },
                        definition.links.joinToString(separator = ", ").takeIf { definition.links.isNotEmpty() },
                    ).joinToString(separator = "<br>")

                    DefinitionEntity.new {
                        word = wordEntity
                        this.zhuyin = zhuyin!!
                        content = definitionContent
                        dictionary = SourceDictionary.MOE
                    }
                }
            }
        }
    }

    val lacEntries =
        LacParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/lac.csv.gz")))

    transaction {
        lacEntries.forEach { entry ->
            val wordEntity =
                WordEntity.find { WordTable.traditional eq entry.headword }.firstOrNull() ?: WordEntity.new {
                    traditional = entry.headword
                    characterCount = entry.headword.length
                }

            DefinitionEntity.new {
                word = wordEntity
                this.zhuyin = entry.zhuyin
                content = entry.definitions.joinToString(separator = "<br>")
                dictionary = SourceDictionary.LAC
            }
        }
    }
}