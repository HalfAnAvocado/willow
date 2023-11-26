package com.marvinelsen.willow

import com.marvinelsen.willow.dictionary.AsyncDictionary
import com.marvinelsen.willow.dictionary.database.DatabaseManager
import com.marvinelsen.willow.sources.cedict.CedictParser
import com.marvinelsen.willow.sources.lac.LacParser
import com.marvinelsen.willow.sources.moe.MoeParser
import java.util.zip.GZIPInputStream
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.text.Font
import javafx.stage.Stage

class WillowApplication : Application() {
    private val interFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/InterVariable.ttf")!!.toExternalForm(), 12.0)
    private val twKaiFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/tw-kai.ttf")!!.toExternalForm(), 12.0)
    private val notoSansCjk =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/NotoSansTC-VF.ttf")!!.toExternalForm(), 12.0)

    init {
        DatabaseManager.init()

        if (!DatabaseManager.doesDatabaseExist()) {
            val cedictEntries =
                CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/cedict_1_0_ts_utf-8_mdbg.txt.gz")))
            val moeEntries =
                MoeParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/moedict.json.gz")))
            val lacEntries =
                LacParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("data/lac.csv.gz")))

            DatabaseManager.createDatabase(cedictEntries, moeEntries, lacEntries)
        }
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

    override fun stop() {
        AsyncDictionary.shutdownExecutor()
    }

    companion object {
        private const val WINDOW_TITLE = "Willow"
        private const val WINDOW_MIN_HEIGHT = 480.0
        private const val WINDOW_MIN_WIDTH = 640.0
    }
}

fun actualMain() {
    Application.launch(WillowApplication::class.java)
}
