package com.marvinelsen.willow.ui.controllers

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.anki.Anki
import com.marvinelsen.willow.config.AnkiConfig
import com.marvinelsen.willow.config.FieldMapping
import com.marvinelsen.willow.dictionary.AsyncDictionary
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.dictionary.SourceDictionary
import com.marvinelsen.willow.ui.cells.EntryCellFactory
import com.marvinelsen.willow.ui.cells.SentenceCellFactory
import com.marvinelsen.willow.ui.dialogs.AddSentenceDialog
import com.marvinelsen.willow.ui.dialogs.CreateAnkiNoteDialog
import com.marvinelsen.willow.ui.undo.SearchCommand
import com.marvinelsen.willow.ui.undo.UndoManager
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.web.WebView
import kotlinx.coroutines.runBlocking

class MainController {
    lateinit var root: VBox

    lateinit var menuItemCopyZhuyin: MenuItem
    lateinit var menuItemCreateAnkiNote: MenuItem
    lateinit var menuItemCopyHeadword: MenuItem

    lateinit var buttonNext: Button
    lateinit var buttonBack: Button
    lateinit var textFieldSearch: TextField

    lateinit var listViewEntries: ListView<Entry>
    lateinit var listViewCharacters: ListView<Entry>
    lateinit var listViewWordsContainingEntries: ListView<Entry>
    lateinit var listViewSentences: ListView<Sentence>

    lateinit var labelNoEntriesFound: Label
    lateinit var labelNoCharactersFound: Label
    lateinit var labelNoWordsContainingFound: Label
    lateinit var labelNoSentencesFound: Label

    lateinit var webViewDefinitions: WebView
    lateinit var labelStatus: Label

    lateinit var textFlowHeadWord: TextFlow
    lateinit var labelHeadwordPronunciation: Label

    lateinit var tabPaneEntryView: TabPane

    private val systemClipboard = Clipboard.getSystemClipboard()

    private val selectedEntryProperty: SimpleObjectProperty<Entry?> = SimpleObjectProperty(null)
    private val isEntrySelectedBinding = selectedEntryProperty.isNotNull

    private var previousSearch: String = ""

    lateinit var buttonSearch: Button

    private val ankiConfig = AnkiConfig(
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

    fun onMenuItemCopySentence(sentence: Sentence?) {
        if (sentence == null) return

        val clipboardContent = ClipboardContent()
        clipboardContent.putString(sentence.traditional)
        systemClipboard.setContent(clipboardContent)
        setStatus("Copied sentence to clipboard.")
    }

    fun onMenuItemCreateAnkiNoteWithSentence(sentence: Sentence?) {
        if (sentence == null) return

        CreateAnkiNoteDialog(
            owner = root.scene.window,
            entry = selectedEntryProperty.value!!,
            exampleSentence = sentence.traditional
        ).showAndWait().ifPresent {
            runBlocking {
                val anki = Anki(ankiConfig)
                anki.createNote(selectedEntryProperty.value!!, it.definitionSourceDictionary, it.exampleSentence)
            }
        }
    }

    fun initialize() {
        selectedEntryProperty.addListener { _, _, newValue ->
            displayEntry(newValue)
        }

        listViewEntries.apply {
            cellFactory = EntryCellFactory()
            items = FXCollections.observableArrayList()
            selectionModel.selectedItemProperty()
                .addListener { _, _, newEntry ->
                    tabPaneEntryView.selectionModel.selectFirst()

                    selectEntry(newEntry)
                }
        }

        listViewWordsContainingEntries.apply {
            cellFactory = EntryCellFactory()
            items = FXCollections.observableArrayList()
            selectionModel.selectedItemProperty()
                .addListener { _, _, newEntry ->
                    if (newEntry == null) return@addListener

                    tabPaneEntryView.selectionModel.selectFirst()

                    UndoManager.execute(
                        SearchCommand(
                            this@MainController,
                            listViewEntries.selectionModel.selectedIndex,
                            textFieldSearch.text,
                            newEntry.traditional
                        )
                    )
                    textFieldSearch.text = newEntry.traditional
                }
        }

        listViewCharacters.apply {
            cellFactory = EntryCellFactory()
            items = FXCollections.observableArrayList()
            selectionModel.selectedItemProperty().addListener { _, oldEntry, newEntry ->
                if (newEntry == null) return@addListener

                tabPaneEntryView.selectionModel.selectFirst()

                UndoManager.execute(
                    SearchCommand(
                        this@MainController,
                        listViewEntries.selectionModel.selectedIndex,
                        textFieldSearch.text,
                        newEntry.traditional
                    )
                )
                textFieldSearch.text = newEntry.traditional
            }
        }

        listViewSentences.apply {
            cellFactory = SentenceCellFactory(this@MainController)
            items = FXCollections.observableArrayList()
        }

        labelNoCharactersFound.visibleProperty().bind(Bindings.isEmpty(listViewCharacters.items))
        labelNoEntriesFound.visibleProperty().bind(Bindings.isEmpty(listViewEntries.items))
        labelNoWordsContainingFound.visibleProperty().bind(Bindings.isEmpty(listViewWordsContainingEntries.items))
        labelNoSentencesFound.visibleProperty().bind(Bindings.isEmpty(listViewSentences.items))

        menuItemCopyHeadword.disableProperty().bind(isEntrySelectedBinding.not())
        menuItemCopyZhuyin.disableProperty().bind(isEntrySelectedBinding.not())
        menuItemCreateAnkiNote.disableProperty().bind(isEntrySelectedBinding.not())

        webViewDefinitions.apply {
            isContextMenuEnabled = false
            engine.userStyleSheetLocation =
                WillowApplication::class.java.getResource("stylesheets/definitions.css")!!.toExternalForm()
        }

        buttonBack.disableProperty().bind(UndoManager.canUndoProperty.not())
        buttonNext.disableProperty().bind(UndoManager.canRedoProperty.not())

        tabPaneEntryView.selectionModel.selectedItemProperty().addListener { _, _, selectedTab ->
            if (selectedEntryProperty.value == null) return@addListener

            when (selectedTab.id) {
                "tabPaneCharacters" -> AsyncDictionary.findCharactersOf(selectedEntryProperty.value!!) {
                    listViewCharacters.apply {
                        items.clear()
                        items.addAll(it)
                    }
                }

                "tabPaneWords" -> AsyncDictionary.findEntriesContaining(selectedEntryProperty.value!!) {
                    listViewWordsContainingEntries.apply {
                        items.clear()
                        items.addAll(it)
                    }
                }

                "tabPaneSentences" -> AsyncDictionary.findSentencesFor(selectedEntryProperty.value!!) {
                    listViewSentences.apply {
                        items.clear()
                        items.addAll(it)
                    }
                }

                else -> {}
            }
        }
    }

    fun onMenuItemNewEntryAction() {}
    fun onMenuItemNewSentenceAction() {
        AddSentenceDialog(root.scene.window, systemClipboard.string).showAndWait().ifPresent {
            AsyncDictionary.addUserSentence(it.sentence)
            setStatus("New sentence added to dictionary.")
            if (selectedEntryProperty.value != null) {
                AsyncDictionary.findSentencesFor(selectedEntryProperty.value!!) {
                    listViewSentences.apply {
                        items.clear()
                        items.addAll(it)
                    }
                }
            }
        }
    }

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

    private fun displayEntry(entry: Entry?) {
        textFlowHeadWord.children.clear()
        labelHeadwordPronunciation.text = ""
        webViewDefinitions.engine.loadContent("")

        selectedEntryProperty.value = entry

        if (entry == null) return

        for (character in entry.characters) {
            val characterText = Text(character)
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
    }

    private fun setStatus(status: String) {
        labelStatus.text = status
    }

    private fun selectEntry(entry: Entry?) {
        selectedEntryProperty.value = entry
    }


    fun onMenuItemCreateAnkiNote(actionEvent: ActionEvent) {
        if (!isEntrySelectedBinding.value) return

        CreateAnkiNoteDialog(root.scene.window, selectedEntryProperty.value!!).showAndWait().ifPresent {
            runBlocking {
                val anki = Anki(ankiConfig)
                anki.createNote(
                    entry = selectedEntryProperty.value!!,
                    definitionSource = it.definitionSourceDictionary,
                    exampleSentence = it.exampleSentence
                )
            }
        }
    }

    fun onButtonBackAction(actionEvent: ActionEvent) {
        UndoManager.undo()
    }

    fun onButtonNextAction(actionEvent: ActionEvent) {
        UndoManager.redo()
    }

    fun search(query: String, selectionIndex: Int = 0) {
        previousSearch = query

        if (query.isBlank()) {
            listViewEntries.items.clear()
            return
        }

        AsyncDictionary.search(query) {
            with(listViewEntries) {
                items.clear()
                items.addAll(it)
                selectionModel.select(selectionIndex)
            }
            setStatus("Found ${it.size} matching entries.")
        }
    }

    fun textFieldSearchOnAction() {
        UndoManager.execute(
            SearchCommand(
                this@MainController,
                listViewEntries.selectionModel.selectedIndex,
                previousSearch,
                textFieldSearch.text
            )
        )
    }

    fun onButtonSearchAction() {
        UndoManager.execute(
            SearchCommand(
                this@MainController,
                listViewEntries.selectionModel.selectedIndex,
                previousSearch,
                textFieldSearch.text
            )
        )
    }

    fun setupKeyboardShortcuts(scene: Scene) {
        scene.accelerators.apply {
            put(KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), Runnable { textFieldSearch.requestFocus() })
            put(KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), Runnable { buttonBack.fire() })
            put(KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN), Runnable { buttonNext.fire() })
        }

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, EventHandler {
            when (it.button) {
                MouseButton.FORWARD -> {
                    buttonNext.fire()
                    it.consume()
                }

                MouseButton.BACK -> {
                    buttonBack.fire()
                    it.consume()
                }

                else -> {}
            }
        })
    }
}