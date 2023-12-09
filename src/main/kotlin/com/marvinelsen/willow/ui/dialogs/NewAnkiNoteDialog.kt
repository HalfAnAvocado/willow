package com.marvinelsen.willow.ui.dialogs

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Callback

class NewAnkiNoteDialog(owner: Window?, val entry: Entry, exampleSentence: String = "") :
    Dialog<NewAnkiNoteDialogResult?>() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textFieldHeadword: TextField

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textFieldPronunciation: TextField

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var comboBoxDefinitionSourceDictionary: ComboBox<SourceDictionary>

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var textAreaExampleSentence: TextArea

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var buttonTypeOk: ButtonType

    private val systemClipboard = Clipboard.getSystemClipboard()

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/new-anki-note-dialog.fxml"))
        loader.setController(this)
        val root: DialogPane = loader.load()

        dialogPane = root
        title = "New Anki Note"
        isResizable = true

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        (dialogPane.scene.window as Stage).apply {
            minWidth = 400.0
            minHeight = 250.0
        }

        val buttonOk = root.lookupButton(buttonTypeOk)
        buttonOk.addEventFilter(ActionEvent.ACTION, ::validateUserInput)

        textFieldHeadword.text = entry.traditional
        textFieldPronunciation.text = entry.zhuyin
        comboBoxDefinitionSourceDictionary.items = FXCollections.observableArrayList(entry.availableDefinitionSources)
        comboBoxDefinitionSourceDictionary.selectionModel.selectFirst()
        textAreaExampleSentence.text = exampleSentence

        resultConverter = Callback(::convertToResult)
    }

    private fun validateUserInput(event: ActionEvent) {
        return
    }

    private fun convertToResult(buttonType: ButtonType) =
        when (buttonType) {
            ButtonType.OK -> NewAnkiNoteDialogResult(
                definitionSourceDictionary = comboBoxDefinitionSourceDictionary.selectionModel.selectedItem,
                exampleSentence = textAreaExampleSentence.text
            )

            else -> null
        }

    @Suppress("Unused")
    fun onButtonPasteIntoExampleSentenceAction() {
        textAreaExampleSentence.text = systemClipboard.string
    }
}

data class NewAnkiNoteDialogResult(
    val definitionSourceDictionary: SourceDictionary,
    val exampleSentence: String,
)