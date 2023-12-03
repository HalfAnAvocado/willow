package com.marvinelsen.willow.ui.dialogs

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.Sentence
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.input.Clipboard
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Callback

class AddSentenceDialog(owner: Window?, exampleSentence: String?) : Dialog<Sentence?>() {
    lateinit var textAreaChineseSentence: TextArea

    private val systemClipboard = Clipboard.getSystemClipboard()

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/add-sentence-dialog.fxml"))
        loader.setController(this)

        val root: GridPane = loader.load()

        textAreaChineseSentence.text = exampleSentence ?: ""

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        title = "Add Sentence"
        headerText = "Add an example sentence to the dictionary..."
        isResizable = true

        dialogPane.content = root
        dialogPane.buttonTypes.addAll(ButtonType.CLOSE, ButtonType.OK)

        val dialogStage = dialogPane.scene.window as Stage
        dialogStage.minWidth = 380.0
        dialogStage.minHeight = 250.0

        resultConverter = Callback { buttonType: ButtonType -> returnResult(buttonType) }
    }

    private fun returnResult(buttonType: ButtonType) =
        if (ButtonType.OK == buttonType) Sentence(textAreaChineseSentence.text) else null
}