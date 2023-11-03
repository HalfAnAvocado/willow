package com.marvinelsen.willow

import com.marvinelsen.willow.cedict.CedictParser
import com.marvinelsen.willow.cedict.toEntity
import com.marvinelsen.willow.persistence.cedict.CedictTable
import java.sql.Connection
import java.util.zip.GZIPInputStream
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction


class WillowApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(WillowApplication::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene

        val cedictEntries =
            CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("cedict_1_0_ts_utf-8_mdbg.txt.gz")))
        print(cedictEntries[500])

        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            // addLogger(StdOutSqlLogger)
            SchemaUtils.create(CedictTable)

            cedictEntries.forEach { it.toEntity() }
        }

        stage.show()
    }
}

fun main() {
    Application.launch(WillowApplication::class.java)
}