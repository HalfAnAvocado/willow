package com.marvinelsen.willow.ui.dialogs

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.Definition
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.ui.alerts.alert
import com.marvinelsen.willow.util.PronunciationConverter
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Callback

class NewEntryDialog(owner: Window?) : Dialog<Entry?>() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textFieldHeadword: TextField

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textFieldPronunciation: TextField

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textAreaDefinition: TextArea

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var buttonTypeOk: ButtonType

    private val systemClipboard = Clipboard.getSystemClipboard()

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/new-entry-dialog.fxml"))
        loader.setController(this)
        val root: DialogPane = loader.load()

        dialogPane = root
        title = "New Entry"
        isResizable = true

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        (dialogPane.scene.window as Stage).apply {
            minWidth = DIALOG_MIN_WIDTH
            minHeight = DIALOG_MIN_HEIGHT
        }

        val buttonOk = root.lookupButton(buttonTypeOk)
        buttonOk.addEventFilter(ActionEvent.ACTION, ::validateUserInput)
        buttonOk
            .disableProperty()
            .bind(
                textFieldHeadword.textProperty().isEmpty
                    .or(textFieldPronunciation.textProperty().isEmpty)
                    .or(textAreaDefinition.textProperty().isEmpty)
            )

        resultConverter = Callback(::convertToResult)
    }

    private fun validateUserInput(event: ActionEvent) {
        when {
            textFieldHeadword.text.isBlank() -> alert(Alert.AlertType.ERROR) {
                title = "Invalid Input"
                headerText = "Headword was blank"
                contentText = "Headword was left blank, but should contain at least one Traditional Chinese character."
            }.show()

            textFieldPronunciation.text.isBlank() -> alert(Alert.AlertType.ERROR) {
                title = "Invalid Input"
                headerText = "Pronunciation was blank"
                contentText = "Pronunciation was left blank, but should contain numbered pinyin."
            }.show()

            textAreaDefinition.text.isBlank() -> alert(Alert.AlertType.ERROR) {
                title = "Invalid Input"
                headerText = "Definition was blank"
                contentText =
                    "Definition was left blank, but should contain the Chinese or English definition of the entry."
            }.show()

            else -> return
        }
        event.consume()
    }

    private fun convertToResult(buttonType: ButtonType) =
        when (buttonType) {
            ButtonType.OK -> {
                Entry(
                    traditional = textFieldHeadword.text,
                    zhuyin = PronunciationConverter.convertToZhuyin(textFieldPronunciation.text),
                    definitions = mapOf(
                        SourceDictionary.USER to listOf(
                            Definition(
                                shortDefinition = textAreaDefinition.text,
                                htmlDefinition = textAreaDefinition.text,
                                sourceDictionary = SourceDictionary.USER
                            )
                        )
                    )
                )
            }

            else -> null
        }

    @Suppress("Unused")
    fun onButtonPasteIntoHeadwordAction() {
        textFieldHeadword.text = systemClipboard.string
    }

    @Suppress("Unused")
    fun onButtonPasteIntoPronunciationAction() {
        textFieldPronunciation.text = systemClipboard.string
    }

    @Suppress("Unused")
    fun onButtonPasteIntoDefinitionAction() {
        textAreaDefinition.text = systemClipboard.string
    }

    companion object {
        private const val DIALOG_MIN_HEIGHT = 250.0
        private const val DIALOG_MIN_WIDTH = 380.0
    }
}
