package com.marvinelsen.willow

import com.marvinelsen.willow.persistence.cedict.DefinitionEntity
import com.marvinelsen.willow.persistence.cedict.DefinitionTable
import com.marvinelsen.willow.persistence.cedict.WordEntity
import com.marvinelsen.willow.persistence.cedict.WordTable
import com.marvinelsen.willow.serialization.cedict.CedictParser
import com.marvinelsen.willow.serialization.moe.MoeParser
import com.marvinelsen.willow.service.objects.Dictionary
import java.sql.Connection
import java.util.zip.GZIPInputStream
import javafx.application.Application
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
    private val notoSansTcFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/notosanstc.otf")!!.toExternalForm(), 12.0)

    init {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
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
    }
}

fun main() {
    Application.launch(WillowApplication::class.java)
}

fun createDatabaseTables() {
    val cedictEntries =
        CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/cedict_1_0_ts_utf-8_mdbg.txt.gz")))

    val moeEntries =
        MoeParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/moedict.json.gz")))

    transaction {
        // addLogger(StdOutSqlLogger)
        SchemaUtils.create(WordTable, DefinitionTable)

        cedictEntries.groupBy { it.traditional }.values.forEach {
            val wordEntity = WordEntity.new {
                traditional = it.first().traditional
                simplified = it.first().simplified
            }

            for (cedictEntry in it) {
                DefinitionEntity.new {
                    word = wordEntity
                    numberedPinyin = cedictEntry.numberedPinyin
                    numberedPinyinTaiwan = cedictEntry.numberedPinyinTaiwan
                    content = cedictEntry.definitions
                    dictionary = Dictionary.CEDICT
                }
            }
        }
    }
}