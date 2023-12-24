package com.marvinelsen.willow

import com.marvinelsen.willow.dictionary.database.DatabaseManager
import com.marvinelsen.willow.sources.cedict.CedictParser
import com.marvinelsen.willow.sources.cedict.toEntries
import com.marvinelsen.willow.sources.lac.LacParser
import com.marvinelsen.willow.sources.lac.toEntries
import com.marvinelsen.willow.sources.moe.MoeParser
import com.marvinelsen.willow.sources.moe.toEntries
import com.marvinelsen.willow.sources.tatoeba.TatoebaParser
import com.marvinelsen.willow.sources.tatoeba.toSentences
import com.marvinelsen.willow.ui.controllers.MainController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import java.util.zip.GZIPInputStream

class WillowApplication : Application() {
    companion object {
        private const val WINDOW_TITLE = "Willow"
        private const val WINDOW_MIN_HEIGHT = 480.0
        private const val WINDOW_MIN_WIDTH = 640.0
        private const val WINDOW_WIDTH = 600.0
        private const val WINDOW_HEIGHT = 400.0

        private const val FONT_SIZE = 12.0
    }

    init {
        loadFonts()

        DatabaseManager.init()

        if (!DatabaseManager.doesDatabaseExist()) {
            val cedictEntries =
                CedictParser.parse(
                    GZIPInputStream(
                        WillowApplication::class.java.getResourceAsStream("data/cedict_1_0_ts_utf-8_mdbg.txt.gz")
                    )
                )
            val moeEntries =
                MoeParser.parse(
                    GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/moedict.json.gz"))
                )
            val lacEntries =
                LacParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/lac.csv.gz")))
            val tatoebaSentences =
                TatoebaParser.parse(
                    GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/cmn_sentences_tw.tsv.gz"))
                )

            DatabaseManager.createSchema()
            DatabaseManager.insertEntries(
                cedictEntries.toEntries() +
                    lacEntries.toEntries() +
                    moeEntries.toEntries()
            )
            DatabaseManager.insertSentences(tatoebaSentences.toSentences())
        }
    }

    private fun loadFonts() {
        Font.loadFont(WillowApplication::class.java.getResourceAsStream("fonts/inter.ttf"), FONT_SIZE)
        Font.loadFont(WillowApplication::class.java.getResourceAsStream("fonts/tw-kai.ttf"), FONT_SIZE)
        Font.loadFont(WillowApplication::class.java.getResourceAsStream("fonts/noto-sans-tc.ttf"), FONT_SIZE)
    }

    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader()
        val root = fxmlLoader.load(WillowApplication::class.java.getResourceAsStream("fxml/main-view.fxml")) as VBox
        val controller = fxmlLoader.getController<MainController>()

        val primaryScene = Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT)
        primaryScene.stylesheets.add(WillowApplication::class.java.getResource("css/main.css")?.toExternalForm())

        controller.setupKeyboardShortcuts(primaryScene)

        with(primaryStage) {
            title = WINDOW_TITLE
            minWidth = WINDOW_MIN_WIDTH
            minHeight = WINDOW_MIN_HEIGHT
            scene = primaryScene
            show()
        }
    }
}

fun actualMain() {
    Application.launch(WillowApplication::class.java)
}
