package com.marvinelsen.willow

import com.marvinelsen.willow.persistence.cedict.CedictEntity
import com.marvinelsen.willow.persistence.cedict.CedictTable
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
    lateinit var listViewDictionary: ListView<String>

    fun onMenuItemNewEntryAction(actionEvent: ActionEvent?) {}
    fun onMenuItemNewSentenceAction(actionEvent: ActionEvent?) {}
    fun onMenuItemSettingsAction(actionEvent: ActionEvent?) {}
    fun onMenuItemQuitAction(actionEvent: ActionEvent?) {}
    fun onMenuItemCopyHeadwordAction(actionEvent: ActionEvent?) {}
    fun onMenuItemCopyPronunciationAction(actionEvent: ActionEvent?) {}
    fun onMenuItemAboutAction(actionEvent: ActionEvent?) {}
    fun showSelectedWordContextMenu(actionEvent: ContextMenuEvent?) {}

    fun onTextFieldSearchAction(actionEvent: ActionEvent?) {
        val words = transaction {
            CedictEntity.find { CedictTable.traditional like "${textFieldSearch.text}%" }.limit(100).toList()
                .map { it.traditional }
        }
        listViewDictionary.items = FXCollections.observableList(words)
    }
}
