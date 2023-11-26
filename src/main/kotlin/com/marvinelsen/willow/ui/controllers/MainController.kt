package com.marvinelsen.willow.ui.controllers

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.anki.Anki
import com.marvinelsen.willow.config.AnkiConfig
import com.marvinelsen.willow.config.FieldMapping
import com.marvinelsen.willow.dictionary.AsyncDictionary
import com.marvinelsen.willow.dictionary.Dictionary
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.ui.cells.EntryCellFactory
import com.marvinelsen.willow.ui.dialogs.AddAnkiFlashcard
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.web.WebView
import kotlinx.coroutines.runBlocking


class MainController {
    lateinit var root: VBox

    lateinit var menuItemCopyZhuyin: MenuItem
    lateinit var menuItemNewAnkiFlashcard: MenuItem
    lateinit var menuItemCopyHeadword: MenuItem

    lateinit var textFieldSearch: TextField

    lateinit var listViewEntries: ListView<Entry>
    lateinit var listViewCharacters: ListView<Entry>
    lateinit var listViewWordsContainingEntries: ListView<Entry>
    lateinit var listViewSentences: ListView<String>

    lateinit var labelNoEntriesFound: Label
    lateinit var labelNoCharactersFound: Label
    lateinit var labelNoWordsContainingFound: Label
    lateinit var labelNoSentencesFound: Label

    lateinit var webViewDefinitions: WebView
    lateinit var labelStatus: Label

    lateinit var textFlowHeadWord: TextFlow
    lateinit var labelHeadwordPronunciation: Label

    private val systemClipboard = Clipboard.getSystemClipboard()

    private val selectedEntryProperty: SimpleObjectProperty<Entry?> = SimpleObjectProperty(null)
    private val isEntrySelectedBinding = selectedEntryProperty.isNotNull

    fun initialize() {
        listViewEntries.apply {
            cellFactory = EntryCellFactory()
            items = FXCollections.observableArrayList(Dictionary.search("柳"))
            selectionModel.selectedItemProperty().addListener { _, _, newEntry -> displayEntryDefinitions(newEntry) }
            selectionModel.selectFirst()
        }

        listViewWordsContainingEntries.apply {
            cellFactory = EntryCellFactory()
            items = FXCollections.observableArrayList()
        }

        listViewCharacters.apply {
            cellFactory = EntryCellFactory()
            items = FXCollections.observableArrayList()
        }

        textFieldSearch.textProperty().addListener { _, _, newValue ->
            if (newValue.isBlank()) return@addListener

            AsyncDictionary.search(textFieldSearch.text) {
                with(listViewEntries) {
                    items.clear()
                    items.addAll(it)
                    selectionModel.selectFirst()
                }
                setStatus("Found ${it.size} matching entries.")
            }
        }

        labelNoCharactersFound.visibleProperty().bind(Bindings.isEmpty(listViewCharacters.items))
        labelNoEntriesFound.visibleProperty().bind(Bindings.isEmpty(listViewEntries.items))
        labelNoWordsContainingFound.visibleProperty().bind(Bindings.isEmpty(listViewWordsContainingEntries.items))
        labelNoSentencesFound.visibleProperty().bind(Bindings.isEmpty(listViewSentences.items))

        menuItemCopyHeadword.disableProperty().bind(isEntrySelectedBinding.not())
        menuItemCopyZhuyin.disableProperty().bind(isEntrySelectedBinding.not())
        menuItemNewAnkiFlashcard.disableProperty().bind(isEntrySelectedBinding.not())

        webViewDefinitions.apply {
            isContextMenuEnabled = false
            engine.userStyleSheetLocation =
                WillowApplication::class.java.getResource("stylesheets/definitions.css")!!.toExternalForm()
        }
    }

    fun onMenuItemNewEntryAction() {}
    fun onMenuItemNewSentenceAction() {}
    fun onMenuItemSettingsAction() {}

    fun onMenuItemQuitAction() {
        Platform.exit()
    }

    fun onMenuItemCopyHeadwordAction() {
        if (!isEntrySelectedBinding.value) return

        val clipboardContent = ClipboardContent()
        clipboardContent.putString(selectedEntryProperty.value!!.traditional)
        systemClipboard.setContent(clipboardContent)

        setStatus("Copied headword to clipboard.")
    }

    fun onMenuItemCopyZhuyinAction() {
        if (!isEntrySelectedBinding.value) return

        val clipboardContent = ClipboardContent()
        clipboardContent.putString(selectedEntryProperty.value!!.zhuyin)
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
        listViewWordsContainingEntries.items.clear()

        selectedEntryProperty.value = entry

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
            listViewCharacters.apply {
                items.clear()
                items.addAll(it)
            }
        }

        AsyncDictionary.findEntriesContaining(entry) {
            listViewWordsContainingEntries.apply {
                items.clear()
                items.addAll(it)
            }
        }
    }

    private fun setStatus(status: String) {
        labelStatus.text = status
    }

    fun onMenuItemNewAnkiFlashcardAction(actionEvent: ActionEvent) {
        if (!isEntrySelectedBinding.value) return

        AddAnkiFlashcard(root.scene.window, selectedEntryProperty.value!!).showAndWait().ifPresent {
            runBlocking {
                val ankiConfig = AnkiConfig(
                    ankiConnectUrl = "http://127.0.0.1:8765",
                    deckName = "華語::閱讀",
                    modelName = "中文",
                    fields = FieldMapping(
                        traditional = "單詞",
                        zhuyin = "注音",
                        definition = "中文釋義",
                        exampleSentence = "例句"
                    )
                )
                val anki = Anki(ankiConfig)
                anki.addNoteFor(selectedEntryProperty.value!!, it.definitionSourceDictionary, it.exampleSentence)
            }
        }
    }
}