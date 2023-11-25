package com.marvinelsen.willow.ui.controllers

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.AsyncDictionary
import com.marvinelsen.willow.dictionary.Dictionary
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.ui.cells.EntryCellFactory
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.web.WebView


class MainController {
    lateinit var webViewDefinitions: WebView
    lateinit var listViewCharacters: ListView<Entry>
    lateinit var listViewEntries: ListView<Entry>
    lateinit var labelStatus: Label
    lateinit var labelHeadwordPronunciation: Label
    lateinit var textFlowHeadWord: TextFlow
    lateinit var textFieldSearch: TextField
    lateinit var listViewDictionary: ListView<Entry>

    private val systemClipboard = Clipboard.getSystemClipboard()


    fun initialize() {
        listViewDictionary.cellFactory = EntryCellFactory()
        listViewEntries.cellFactory = EntryCellFactory()
        listViewCharacters.cellFactory = EntryCellFactory()

        listViewDictionary.items = FXCollections.observableArrayList()
        listViewEntries.items = FXCollections.observableArrayList()
        listViewCharacters.items = FXCollections.observableArrayList()

        listViewDictionary.items.addAll(Dictionary.search("柳"))
        listViewDictionary.selectionModel
            .selectedItemProperty()
            .addListener { _, _, newEntry -> displayEntryDefinitions(newEntry) }
        listViewDictionary.selectionModel.selectFirst()

        textFieldSearch.textProperty().addListener { _, _, newValue ->
            if (newValue.isBlank()) return@addListener

            AsyncDictionary.search(textFieldSearch.text) {
                listViewDictionary.items.clear()
                listViewDictionary.items.addAll(it)
                listViewDictionary.selectionModel.selectFirst()
                setStatus("Found ${it.size} matching entries.")
            }
        }
        webViewDefinitions.isContextMenuEnabled = false
        webViewDefinitions.engine.userStyleSheetLocation =
            WillowApplication::class.java.getResource("stylesheets/definitions.css")!!.toExternalForm()
    }

    fun onMenuItemNewEntryAction() {}
    fun onMenuItemNewSentenceAction() {}
    fun onMenuItemSettingsAction() {}
    fun onMenuItemQuitAction() {}

    fun onMenuItemCopyHeadwordAction() {
        val clipboardContent = ClipboardContent()
        clipboardContent.putString(listViewDictionary.selectionModel.selectedItem.traditional)
        systemClipboard.setContent(clipboardContent)

        setStatus("Copied headword to clipboard.")
    }

    fun onMenuItemCopyPronunciationAction() {
        val clipboardContent = ClipboardContent()
        clipboardContent.putString(labelHeadwordPronunciation.text)
        systemClipboard.setContent(clipboardContent)

        setStatus("Copied pronunciation to clipboard.")
    }

    fun onMenuItemAboutAction() {}
    fun showSelectedWordContextMenu() {}

    private fun displayEntryDefinitions(entry: Entry?) {
        textFlowHeadWord.children.clear()
        labelHeadwordPronunciation.text = ""
        webViewDefinitions.engine.loadContent("")
        listViewCharacters.items.clear()
        listViewEntries.items.clear()

        if (entry == null) return

        val characters = entry.traditional.split("")
        for (i in characters.indices) {
            val characterText = Text(characters[i])
            characterText.fill = Color.web("#000")
            characterText.styleClass.add("headword")
            textFlowHeadWord.children.add(characterText)
        }
        val cedictDefinitions = entry.definitions[SourceDictionary.CEDICT]
        val moeDefinitions = entry.definitions[SourceDictionary.MOE]
        val lacDefinitions = entry.definitions[SourceDictionary.LAC]

        labelHeadwordPronunciation.text = entry.zhuyin

        val cedictContent: String? =
            cedictDefinitions?.joinToString(prefix = "<h1>CC-CEDICT</h1>", separator = "<br>") { it.htmlDefinition }
        val moeContent: String? =
            moeDefinitions?.joinToString(prefix = "<h1>MoE</h1>", separator = "<hr>") { it.htmlDefinition }
        val lacContent: String? =
            lacDefinitions?.joinToString(prefix = "<h1>LAC</h1>", separator = "<hr>") { it.htmlDefinition }

        webViewDefinitions.engine.loadContent(
            listOfNotNull(lacContent, moeContent, cedictContent).joinToString(separator = "<hr>")
        )

        AsyncDictionary.findCharactersOf(entry) {
            listViewCharacters.items.clear()
            listViewCharacters.items.addAll(it)
        }

        AsyncDictionary.findEntriesContaining(entry) {
            listViewEntries.items.clear()
            listViewEntries.items.addAll(it)
        }
    }

    private fun setStatus(status: String) {
        labelStatus.text = status
    }
}
