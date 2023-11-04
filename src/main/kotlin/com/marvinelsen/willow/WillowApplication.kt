package com.marvinelsen.willow

import com.marvinelsen.willow.cedict.CedictParser
import com.marvinelsen.willow.cedict.toEntity
import com.marvinelsen.willow.persistence.cedict.CedictTable
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
    private val twKaiFont =  Font.loadFont(WillowApplication::class.java.getResource("fonts/tw-kai.ttf")!!.toExternalForm(), 12.0)
    private val notoSansTcFont =  Font.loadFont(WillowApplication::class.java.getResource("fonts/notosanstc.otf")!!.toExternalForm(), 12.0)

    override fun start(stage: Stage) {
        stage.title = WINDOW_TITLE
        stage.minWidth = WINDOW_MIN_WIDTH
        stage.minHeight = WINDOW_MIN_HEIGHT

        val fxmlLoader = FXMLLoader(WillowApplication::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 600.0, 400.0)
        scene.stylesheets.add(WillowApplication::class.java.getResource("stylesheets/main.css")!!.toExternalForm());
        stage.scene = scene

        val cedictEntries =
            CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("cedict_1_0_ts_utf-8_mdbg.txt.gz")))
        print(cedictEntries[500])

        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            // addLogger(StdOutSqlLogger)
            SchemaUtils.create(CedictTable)

            // cedictEntries.forEach { it.toEntity() }
        }

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