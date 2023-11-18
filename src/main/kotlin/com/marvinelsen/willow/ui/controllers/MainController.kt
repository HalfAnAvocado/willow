package com.marvinelsen.willow.ui.controllers

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.dictionary.AsyncDictionary
import com.marvinelsen.willow.dictionary.Dictionary
import com.marvinelsen.willow.dictionary.objects.SourceDictionary
import com.marvinelsen.willow.dictionary.objects.Word
import com.marvinelsen.willow.serialization.cedict.CedictDefinitionFormatter
import com.marvinelsen.willow.ui.cells.WordCellFactory
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
    lateinit var listViewCharacters: ListView<Word>
    lateinit var listViewWords: ListView<Word>
    lateinit var labelStatus: Label
    lateinit var labelHeadwordPronunciation: Label
    lateinit var textFlowHeadWord: TextFlow
    lateinit var textFieldSearch: TextField
    lateinit var listViewDictionary: ListView<Word>

    private val systemClipboard = Clipboard.getSystemClipboard()


    fun initialize() {
        listViewDictionary.cellFactory = WordCellFactory()
        listViewWords.cellFactory = WordCellFactory()
        listViewCharacters.cellFactory = WordCellFactory()

        listViewDictionary.items = FXCollections.observableArrayList()
        listViewWords.items = FXCollections.observableArrayList()
        listViewCharacters.items = FXCollections.observableArrayList()

        listViewDictionary.items.addAll(Dictionary.search("柳"))
        listViewDictionary.selectionModel
            .selectedItemProperty()
            .addListener { observableValue, entry, newEntry -> displayWord(newEntry) }
        listViewDictionary.selectionModel.selectFirst()

        textFieldSearch.textProperty().addListener { observable, oldValue, newValue ->
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

    private fun displayWord(word: Word?) {
        textFlowHeadWord.children.clear()
        labelHeadwordPronunciation.text = ""
        webViewDefinitions.engine.loadContent("")
        listViewCharacters.items.clear()
        listViewWords.items.clear()

        if (word == null) return

        val characters = word.traditional.split("")
        for (i in characters.indices) {
            val characterText = Text(characters[i])
            characterText.fill = Color.web("#000")
            characterText.styleClass.add("headword")
            textFlowHeadWord.children.add(characterText)
        }
        val cedictDefinitions = word.definitions[SourceDictionary.CEDICT]
        val moeDefinitions = word.definitions[SourceDictionary.MOE]
        val lacDefinitions = word.definitions[SourceDictionary.LAC]

        labelHeadwordPronunciation.text = word.zhuyin

        val cedictContent: String? = cedictDefinitions?.let { CedictDefinitionFormatter.formatForDisplay(it) }

        var moeContent: String? = null
        if (moeDefinitions != null) {
            moeContent = buildString {
                append("<h1>MoE</h1>")
                append("<ol>")
                moeDefinitions.forEach {
                    append("<li>")
                    append(it.content)
                    append("</li>")
                }
                append("</ol>")
            }
        }

        var lacContent: String? = null
        if (lacDefinitions != null) {
            lacContent = buildString {
                append("<h1>LAC</h1>")
                append("<ol>")
                lacDefinitions.forEach {
                    append("<ol>")
                    append(it.content)
                    append("</ol>")
                }
                append("</ol>")
            }
        }
        webViewDefinitions.engine.loadContent(
            listOfNotNull(lacContent, moeContent, cedictContent).joinToString(separator = "<hr>")
        )

        AsyncDictionary.findCharactersOf(word) {
            listViewCharacters.items.addAll(it)
        }

        AsyncDictionary.findWordsContaining(word) {
            listViewWords.items.addAll(it)
        }
    }

    private fun setStatus(status: String) {
        labelStatus.text = status
    }
}
