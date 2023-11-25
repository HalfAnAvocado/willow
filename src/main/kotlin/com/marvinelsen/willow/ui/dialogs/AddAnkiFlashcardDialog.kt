package com.marvinelsen.willow.ui.dialogs

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import javafx.collections.FXCollections
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Callback

class AddAnkiFlashcard(owner: Window?, val entry: Entry) : Dialog<AnkiFlashcardDialogResult?>() {
    lateinit var textFieldHeadword: TextField
    lateinit var textFieldZhuyin: TextField
    lateinit var comboBoxDefinitionSourceDictionary: ComboBox<SourceDictionary>
    lateinit var textAreaExampleSentence: TextArea

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/add-anki-note-dialog.fxml"))
        loader.setController(this)
        val root: GridPane = loader.load()

        textFieldHeadword.text = entry.traditional
        textFieldZhuyin.text = entry.zhuyin
        comboBoxDefinitionSourceDictionary.items = FXCollections.observableArrayList(entry.availableDefinitionSources)

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        title = "Add New Anki Flashcard"
        headerText = "Add a note to Anki"
        isResizable = true

        dialogPane.content = root
        dialogPane.buttonTypes.addAll(ButtonType.CLOSE, ButtonType.OK)

        val dialogStage = dialogPane.scene.window as Stage
        dialogStage.minWidth = 380.0
        dialogStage.minHeight = 250.0

        resultConverter = Callback { buttonType: ButtonType -> returnResult(buttonType) }
    }

    private fun returnResult(buttonType: ButtonType) =
        if (ButtonType.OK == buttonType) AnkiFlashcardDialogResult(
            definitionSourceDictionary = comboBoxDefinitionSourceDictionary.selectionModel.selectedItem,
            exampleSentence = textAreaExampleSentence.text
        ) else null
}

data class AnkiFlashcardDialogResult(
    val definitionSourceDictionary: SourceDictionary,
    val exampleSentence: String,
)