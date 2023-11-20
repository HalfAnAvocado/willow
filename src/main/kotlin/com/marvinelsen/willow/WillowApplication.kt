package com.marvinelsen.willow

import com.marvinelsen.willow.dictionary.AsyncDictionary
import com.marvinelsen.willow.persistence.DatabaseManager
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.text.Font
import javafx.stage.Stage

class WillowApplication : Application() {
    private val twKaiFont =
        Font.loadFont(WillowApplication::class.java.getResource("fonts/tw-kai.ttf")!!.toExternalForm(), 12.0)
    private val notoSansTcRegularFont =
        Font.loadFont(
            WillowApplication::class.java.getResource("fonts/NotoSansCJKtc-Regular.otf")!!.toExternalForm(),
            12.0
        )
    private val notoSansTcBoldFont =
        Font.loadFont(
            WillowApplication::class.java.getResource("fonts/NotoSansCJKtc-Bold.otf")!!.toExternalForm(),
            12.0
        )

    init {
        DatabaseManager.init()
        DatabaseManager.createDatabaseIfNotExist()
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
