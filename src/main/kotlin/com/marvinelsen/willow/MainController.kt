package com.marvinelsen.willow

import com.marvinelsen.willow.persistence.cedict.CedictEntity
import com.marvinelsen.willow.persistence.cedict.CedictTable
import com.marvinelsen.willow.ui.cells.WordCellFactory
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.ContextMenuEvent
import org.jetbrains.exposed.sql.transactions.transaction

class MainController {
    @FXML
    lateinit var textFieldSearch: TextField

    @FXML
    lateinit var listViewDictionary: ListView<CedictEntity>

    @FXML
    fun initialize() {
        listViewDictionary.cellFactory = WordCellFactory()

        listViewDictionary.items = search("柳")
        listViewDictionary.selectionModel.selectFirst()


        textFieldSearch.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue.isNotBlank()) {
                listViewDictionary.items = search(newValue)
            }
        }
    }

    fun onMenuItemNewEntryAction(actionEvent: ActionEvent?) {}
    fun onMenuItemNewSentenceAction(actionEvent: ActionEvent?) {}
    fun onMenuItemSettingsAction(actionEvent: ActionEvent?) {}
    fun onMenuItemQuitAction(actionEvent: ActionEvent?) {}
    fun onMenuItemCopyHeadwordAction(actionEvent: ActionEvent?) {}
    fun onMenuItemCopyPronunciationAction(actionEvent: ActionEvent?) {}
    fun onMenuItemAboutAction(actionEvent: ActionEvent?) {}
    fun showSelectedWordContextMenu(actionEvent: ContextMenuEvent?) {}

    fun onTextFieldSearchAction(actionEvent: ActionEvent?) {
        listViewDictionary.items = search(textFieldSearch.text)
    }

    private fun search(query: String) = FXCollections.observableList(transaction {
        CedictEntity.find { CedictTable.traditional like "$query%" }.toList()
    })
}
