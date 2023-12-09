package com.marvinelsen.willow.ui.dialogs

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.ui.alerts.alert
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.control.TextArea
import javafx.scene.input.Clipboard
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Callback

class NewSentenceDialog(owner: Window?) : Dialog<Sentence?>() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textAreaChineseSentence: TextArea

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var buttonTypeOk: ButtonType

    private val systemClipboard = Clipboard.getSystemClipboard()

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/new-sentence-dialog.fxml"))
        loader.setController(this)
        val root: DialogPane = loader.load()

        dialogPane = root
        title = "New Sentence"
        isResizable = true

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        (dialogPane.scene.window as Stage).apply {
            minWidth = 400.0
            minHeight = 250.0
        }

        val buttonOk = root.lookupButton(buttonTypeOk)
        buttonOk.addEventFilter(ActionEvent.ACTION, ::validateUserInput)
        buttonOk.disableProperty().bind(textAreaChineseSentence.textProperty().isEmpty)

        resultConverter = Callback(::convertToResult)
    }

    private fun validateUserInput(event: ActionEvent) {
        when {
            textAreaChineseSentence.text.isBlank() -> alert(Alert.AlertType.ERROR) {
                title = "Invalid Input"
                headerText = "Chinese sentence was blank"
                contentText =
                    "Chinese sentence was left blank, but should contain at least one Traditional Chinese character."
            }.show()

            else -> return
        }
        event.consume()
    }

    private fun convertToResult(buttonType: ButtonType) =
        when (buttonType) {
            ButtonType.OK -> Sentence(textAreaChineseSentence.text)

            else -> null
        }

    @Suppress("Unused")
    fun onButtonPasteIntoChineseSentenceAction() {
        textAreaChineseSentence.text = systemClipboard.string
    }
}