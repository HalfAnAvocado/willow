package com.marvinelsen.willow

import com.marvinelsen.willow.persistence.cedict.CedictEntity
import com.marvinelsen.willow.service.CedictService
import com.marvinelsen.willow.ui.cells.WordCellFactory
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.input.ContextMenuEvent
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.web.WebView

class MainController {
    lateinit var webViewCedict: WebView
    lateinit var titledPaneCedict: TitledPane
    lateinit var webViewMoe: WebView
    lateinit var titledPaneMoe: TitledPane
    lateinit var webViewUser: WebView
    lateinit var titledPaneUser: TitledPane
    lateinit var labelHeadwordPronunciation: Label
    lateinit var textFlowHeadWord: TextFlow
    lateinit var textFieldSearch: TextField
    lateinit var listViewDictionary: ListView<CedictEntity>

    fun initialize() {
        listViewDictionary.cellFactory = WordCellFactory()

        listViewDictionary.items = FXCollections.observableArrayList()
        listViewDictionary.items.addAll(CedictService.search("柳"))
        listViewDictionary.selectionModel
            .selectedItemProperty()
            .addListener { observableValue, entry, newEntry -> displayWord(newEntry) }
        listViewDictionary.selectionModel.selectFirst()

        textFieldSearch.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue.isNotBlank()) {
                listViewDictionary.items.clear()
                listViewDictionary.items.addAll(CedictService.search(textFieldSearch.text))
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
        listViewDictionary.items.clear()
        listViewDictionary.items.addAll(CedictService.search(textFieldSearch.text))
    }

    private fun displayWord(word: CedictEntity) {
        textFlowHeadWord.children.clear()
        webViewCedict.engine.loadContent("")
        titledPaneCedict.isVisible = false
        titledPaneUser.isManaged = false
        webViewUser.engine.loadContent("")
        titledPaneUser.isVisible = false
        titledPaneUser.isManaged = false
        webViewMoe.engine.loadContent("")
        titledPaneMoe.isVisible = false
        titledPaneMoe.isManaged = false
        val characters = word.traditional.split("")
        for (i in characters.indices) {
            val characterText = Text(characters[i])
            characterText.fill = Color.web("#000")
            characterText.styleClass.add("headword")
            textFlowHeadWord.children.add(characterText)
        }
        labelHeadwordPronunciation.text = word.numberedPinyin
        webViewCedict.engine.loadContent(word.definitions.joinToString(separator = "<br>- "))
        titledPaneCedict.isVisible = true
        titledPaneCedict.isManaged = true

    }
}
