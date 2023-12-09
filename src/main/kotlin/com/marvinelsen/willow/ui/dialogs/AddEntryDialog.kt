package com.marvinelsen.willow.ui.dialogs

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.Definition
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.util.PronunciationConverter
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Callback

class AddEntryDialog(owner: Window?) : Dialog<Entry?>() {
    lateinit var textFieldHeadword: TextField
    lateinit var textFieldPronunciation: TextField
    lateinit var textAreaDefinition: TextArea

    private val systemClipboard = Clipboard.getSystemClipboard()

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/add-entry-dialog.fxml"))
        loader.setController(this)

        val root: GridPane = loader.load()

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        title = "Add Entry"
        headerText = "Add a new entry to the dictionary..."
        isResizable = true

        dialogPane.content = root
        dialogPane.buttonTypes.addAll(ButtonType.CLOSE, ButtonType.OK)

        val dialogStage = dialogPane.scene.window as Stage
        dialogStage.minWidth = 380.0
        dialogStage.minHeight = 250.0

        resultConverter = Callback { buttonType: ButtonType -> returnResult(buttonType) }
    }

    private fun returnResult(buttonType: ButtonType) =
        if (ButtonType.OK == buttonType) Entry(
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
        ) else null

    fun onButtonPasteIntoHeadwordAction() {
    }

    fun onButtonPasteIntoPronunciationAction() {
    }

    fun onButtonPasteIntoDefinitionAction() {
    }
}