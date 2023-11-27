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

class CreateAnkiNoteDialog(owner: Window?, val entry: Entry, exampleSentence: String = "") :
    Dialog<CreateAnkiNoteDialogResult?>() {
    lateinit var textFieldHeadword: TextField
    lateinit var textFieldZhuyin: TextField
    lateinit var comboBoxDefinitionSourceDictionary: ComboBox<SourceDictionary>
    lateinit var textAreaExampleSentence: TextArea

    init {
        val loader = FXMLLoader(WillowApplication::class.java.getResource("views/create-anki-note-dialog.fxml"))
        loader.setController(this)
        val root: GridPane = loader.load()

        textFieldHeadword.text = entry.traditional
        textFieldZhuyin.text = entry.zhuyin
        comboBoxDefinitionSourceDictionary.items = FXCollections.observableArrayList(entry.availableDefinitionSources)
        comboBoxDefinitionSourceDictionary.selectionModel.selectFirst()
        textAreaExampleSentence.text = exampleSentence

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)

        title = "Anki Note Creation"
        headerText = "Create a new Anki note..."
        isResizable = true

        dialogPane.content = root
        dialogPane.buttonTypes.addAll(ButtonType.CLOSE, ButtonType.OK)

        val dialogStage = dialogPane.scene.window as Stage
        dialogStage.minWidth = 380.0
        dialogStage.minHeight = 250.0

        resultConverter = Callback { buttonType: ButtonType -> returnResult(buttonType) }
    }

    private fun returnResult(buttonType: ButtonType) =
        if (ButtonType.OK == buttonType) CreateAnkiNoteDialogResult(
            definitionSourceDictionary = comboBoxDefinitionSourceDictionary.selectionModel.selectedItem,
            exampleSentence = textAreaExampleSentence.text
        ) else null
}

data class CreateAnkiNoteDialogResult(
    val definitionSourceDictionary: SourceDictionary,
    val exampleSentence: String,
)