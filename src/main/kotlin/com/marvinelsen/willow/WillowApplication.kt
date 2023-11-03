package com.marvinelsen.willow

import com.marvinelsen.willow.cedict.CedictParser
import java.util.zip.GZIPInputStream
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage



class WillowApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(WillowApplication::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene
        val cedictEntries =
            CedictParser.parse(GZIPInputStream(WillowApplication::class.java.getResourceAsStream("cedict.txt.gz")))
        print(cedictEntries[500])
        stage.show()
    }
}

fun main() {
    Application.launch(WillowApplication::class.java)
}