package com.marvinelsen.willow.ui.controllers

import com.marvinelsen.willow.WillowApplication
import com.marvinelsen.willow.anki.Anki
import com.marvinelsen.willow.config.AnkiConfig
import com.marvinelsen.willow.config.FieldMapping
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.ui.UIDefinitionFormatter
import com.marvinelsen.willow.ui.cells.EntryCellFactory
import com.marvinelsen.willow.ui.cells.SentenceCellFactory
import com.marvinelsen.willow.ui.dialogs.NewAnkiNoteDialog
import com.marvinelsen.willow.ui.dialogs.NewEntryDialog
import com.marvinelsen.willow.ui.dialogs.NewSentenceDialog
import com.marvinelsen.willow.ui.services.AddUserEntryService
import com.marvinelsen.willow.ui.services.AddUserSentenceService
import com.marvinelsen.willow.ui.services.FindCharactersService
import com.marvinelsen.willow.ui.services.FindEntriesContainingService
import com.marvinelsen.willow.ui.services.FindSentencesService
import com.marvinelsen.willow.ui.services.SearchService
import com.marvinelsen.willow.ui.undo.SearchCommand
import com.marvinelsen.willow.ui.undo.UndoManager
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.web.WebView
import kotlinx.coroutines.runBlocking

@Suppress("TooManyFunctions")
class MainController {
    lateinit var root: VBox

    lateinit var menuItemCopyPronunciation: MenuItem
    lateinit var menuItemNewAnkiNote: MenuItem
    lateinit var menuItemCopyHeadword: MenuItem

    lateinit var buttonNext: Button
    lateinit var buttonBack: Button
    lateinit var textFieldSearch: TextField
    lateinit var buttonSearch: Button

    lateinit var listViewEntries: ListView<Entry>
    lateinit var listViewCharacters: ListView<Entry>
    lateinit var listViewWordsContainingEntries: ListView<Entry>
    lateinit var listViewSentences: ListView<Sentence>

    lateinit var progressIndicatorCharacters: ProgressIndicator
    lateinit var progressIndicatorWordsContaining: ProgressIndicator
    lateinit var progressIndicatorSentences: ProgressIndicator
    lateinit var progressIndicatorEntries: ProgressIndicator

    lateinit var labelNoEntriesFound: Label
    lateinit var labelNoCharactersFound: Label
    lateinit var labelNoWordsContainingFound: Label
    lateinit var labelNoSentencesFound: Label

    lateinit var flowPaneSelectedEntry: FlowPane
    lateinit var textFlowHeadWord: TextFlow
    lateinit var labelHeadwordPronunciation: Label
    lateinit var webViewDefinitions: WebView

    lateinit var tabPaneEntryView: TabPane
    lateinit var tabPaneDefinition: Tab
    lateinit var tabPaneSentences: Tab
    lateinit var tabPaneWords: Tab
    lateinit var tabPaneCharacters: Tab

    lateinit var labelStatus: Label

    private val systemClipboard = Clipboard.getSystemClipboard()

    private val selectedEntryProperty: SimpleObjectProperty<Entry?> = SimpleObjectProperty(null)
    private val isEntrySelectedBinding = selectedEntryProperty.isNotNull

    private var previousSearch: String = ""

    private val selectedEntryContextMenu = ContextMenu().apply {
        val menuItemCopyHeadword = MenuItem("Copy Headword").apply {
            onAction = EventHandler(::onMenuItemCopyHeadwordAction)
        }

        val menuItemCopyPronunciation = MenuItem("Copy Pronunciation").apply {
            onAction = EventHandler(::onMenuItemCopyPronunciationAction)
        }

        val menuItemNewAnkiNote = MenuItem("New Anki Note...").apply {
            onAction = EventHandler(::onMenuItemNewAnkiNoteAction)
        }

        items.addAll(menuItemCopyHeadword, menuItemCopyPronunciation, SeparatorMenuItem(), menuItemNewAnkiNote)
    }

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

    fun onMenuItemNewAnkiNoteWithSentence(sentence: Sentence?) {
        if (sentence == null) return

        NewAnkiNoteDialog(
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

        initializeNoDataFoundLabels()
        initializeProgressIndicators()
        initializeServices()
        initializeMenuItems()
        initializeSearch()
        initializeListViews()
        initializeTabPane()
        initializeDefinitionWebView()
    }

    private fun initializeListViews() {
        listViewEntries.disableProperty()
            .bind(Bindings.isEmpty(listViewEntries.items).or(SearchService.runningProperty()))
        listViewSentences.disableProperty()
            .bind(Bindings.isEmpty(listViewSentences.items).or(FindSentencesService.runningProperty()))
        listViewWordsContainingEntries.disableProperty()
            .bind(
                Bindings
                    .isEmpty(listViewWordsContainingEntries.items)
                    .or(FindEntriesContainingService.runningProperty())
            )
        listViewCharacters.disableProperty()
            .bind(Bindings.isEmpty(listViewCharacters.items).or(FindCharactersService.runningProperty()))

        listViewEntries.apply {
            cellFactory = EntryCellFactory(this@MainController)
            selectionModel.selectedItemProperty()
                .addListener { _, _, newEntry ->
                    tabPaneEntryView.selectionModel.selectFirst()
                    selectEntry(newEntry)
                }
        }

        listViewWordsContainingEntries.apply {
            cellFactory = EntryCellFactory(this@MainController)
            selectionModel.selectedItemProperty().addListener { _, _, newEntry ->
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
            cellFactory = EntryCellFactory(this@MainController)
            selectionModel.selectedItemProperty().addListener { _, _, newEntry ->
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
        }
    }

    private fun initializeServices() {
        SearchService.onSucceeded = EventHandler {
            val (selectionIndex, entries) = SearchService.value
            listViewEntries.items.setAll(entries)
            listViewEntries.selectionModel.select(selectionIndex)
            setStatus("Found ${entries.size} matching entries.")
        }

        FindCharactersService.onSucceeded = EventHandler {
            listViewCharacters.items.setAll(FindCharactersService.value)
        }

        FindEntriesContainingService.onSucceeded = EventHandler {
            listViewWordsContainingEntries.items.setAll(FindEntriesContainingService.value)
        }

        FindSentencesService.onSucceeded = EventHandler {
            listViewSentences.items.setAll(FindSentencesService.value)
        }

        AddUserSentenceService.onSucceeded = EventHandler {
            setStatus("New sentence added to dictionary.")
            if (selectedEntryProperty.value != null) {
                FindSentencesService.selectedEntry = selectedEntryProperty.value!!
                FindSentencesService.restart()
            }
        }

        AddUserEntryService.onSucceeded = EventHandler {
            setStatus("New entry added to dictionary.")
        }
    }

    private fun initializeNoDataFoundLabels() {
        labelNoCharactersFound
            .visibleProperty()
            .bind(Bindings.isEmpty(listViewCharacters.items).and(FindCharactersService.runningProperty().not()))
        labelNoEntriesFound
            .visibleProperty()
            .bind(Bindings.isEmpty(listViewEntries.items).and(SearchService.runningProperty().not()))
        labelNoWordsContainingFound
            .visibleProperty()
            .bind(
                Bindings
                    .isEmpty(listViewWordsContainingEntries.items)
                    .and(FindEntriesContainingService.runningProperty().not())
            )
        labelNoSentencesFound
            .visibleProperty()
            .bind(Bindings.isEmpty(listViewSentences.items).and(FindSentencesService.runningProperty().not()))
    }

    private fun initializeProgressIndicators() {
        progressIndicatorEntries.visibleProperty().bind(SearchService.runningProperty())
        progressIndicatorCharacters.visibleProperty().bind(FindCharactersService.runningProperty())
        progressIndicatorWordsContaining.visibleProperty().bind(FindEntriesContainingService.runningProperty())
        progressIndicatorSentences.visibleProperty().bind(FindSentencesService.runningProperty())
    }

    private fun initializeDefinitionWebView() {
        webViewDefinitions.apply {
            isContextMenuEnabled = false
            engine.userStyleSheetLocation =
                WillowApplication::class.java.getResource("css/definitions.css")!!.toExternalForm()
        }
    }

    private fun initializeSearch() {
        buttonBack.disableProperty().bind(UndoManager.canUndoProperty.not())
        buttonNext.disableProperty().bind(UndoManager.canRedoProperty.not())
        buttonSearch.disableProperty().bind(textFieldSearch.textProperty().isEmpty)

        textFieldSearch.text = "柳"
        previousSearch = "柳"
        search("柳")
    }

    private fun initializeMenuItems() {
        menuItemCopyHeadword.disableProperty().bind(isEntrySelectedBinding.not())
        menuItemCopyPronunciation.disableProperty().bind(isEntrySelectedBinding.not())
        menuItemNewAnkiNote.disableProperty().bind(isEntrySelectedBinding.not())
    }

    private fun initializeTabPane() {
        tabPaneEntryView.disableProperty().bind(isEntrySelectedBinding.not())
        tabPaneDefinition.disableProperty().bind(isEntrySelectedBinding.not())
        tabPaneSentences.disableProperty().bind(isEntrySelectedBinding.not())
        tabPaneWords.disableProperty().bind(isEntrySelectedBinding.not())
        tabPaneCharacters.disableProperty().bind(isEntrySelectedBinding.not())

        tabPaneEntryView.selectionModel.selectedItemProperty().addListener { _, _, selectedTab ->
            if (selectedEntryProperty.value == null) return@addListener

            when (selectedTab.id) {
                "tabPaneCharacters" -> {
                    if (FindCharactersService.selectedEntry != selectedEntryProperty.value!!) {
                        FindCharactersService.selectedEntry = selectedEntryProperty.value!!
                        FindCharactersService.restart()
                    }
                }

                "tabPaneWords" -> {
                    if (FindEntriesContainingService.selectedEntry != selectedEntryProperty.value!!) {
                        FindEntriesContainingService.selectedEntry = selectedEntryProperty.value!!
                        FindEntriesContainingService.restart()
                    }
                }

                "tabPaneSentences" -> {
                    if (FindSentencesService.selectedEntry != selectedEntryProperty.value!!) {
                        FindSentencesService.selectedEntry = selectedEntryProperty.value!!
                        FindSentencesService.restart()
                    }
                }

                else -> {}
            }
        }
    }

    fun onMenuItemNewEntryAction() {
        NewEntryDialog(root.scene.window).showAndWait().ifPresent {
            AddUserEntryService.userEntry = it
            AddUserEntryService.restart()
        }
    }

    fun onMenuItemNewSentenceAction() {
        NewSentenceDialog(root.scene.window).showAndWait().ifPresent {
            AddUserSentenceService.userSentence = it
            AddUserSentenceService.restart()
        }
    }

    fun onMenuItemSettingsAction() {
        println("TODO")
    }

    fun onMenuItemQuitAction() {
        Platform.exit()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMenuItemCopyHeadwordAction(event: ActionEvent) {
        if (!isEntrySelectedBinding.value) return
        copyHeadword(selectedEntryProperty.value!!)
    }

    fun copyHeadword(entry: Entry?) {
        if (entry == null) return

        val clipboardContent = ClipboardContent()
        clipboardContent.putString(entry.traditional)
        systemClipboard.setContent(clipboardContent)

        setStatus("Copied headword to clipboard.")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMenuItemCopyPronunciationAction(event: ActionEvent) {
        if (!isEntrySelectedBinding.value) return
        copyPronunciation(selectedEntryProperty.value!!)
    }

    fun copyPronunciation(entry: Entry?) {
        if (entry == null) return

        val clipboardContent = ClipboardContent()
        clipboardContent.putString(entry.zhuyin)
        systemClipboard.setContent(clipboardContent)

        setStatus("Copied pronunciation to clipboard.")
    }

    fun onMenuItemAboutAction() {
        println("TODO")
    }

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

        labelHeadwordPronunciation.text = entry.accentedPinyin
        webViewDefinitions.engine.loadContent(UIDefinitionFormatter.format(entry))
    }

    private fun setStatus(status: String) {
        labelStatus.text = status
    }

    private fun selectEntry(entry: Entry?) {
        selectedEntryProperty.value = entry
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMenuItemNewAnkiNoteAction(event: ActionEvent) {
        if (!isEntrySelectedBinding.value) return

        showNewAnkiNoteDialog(selectedEntryProperty.value!!)
    }

    fun showNewAnkiNoteDialog(entry: Entry?) {
        if (entry == null) return

        NewAnkiNoteDialog(root.scene.window, entry).showAndWait().ifPresent {
            runBlocking {
                val anki = Anki(ankiConfig)
                anki.createNote(
                    entry = entry,
                    definitionSource = it.definitionSourceDictionary,
                    exampleSentence = it.exampleSentence
                )
            }
        }
    }

    fun onButtonBackAction() {
        UndoManager.undo()
    }

    fun onButtonNextAction() {
        UndoManager.redo()
    }

    fun search(searchQuery: String, selectionIndex: Int = 0) {
        require(searchQuery.isNotBlank()) { "Expected searchQuery to not be blank, but was blank" }
        require(
            selectionIndex >= 0
        ) { "Expected selectionIndex to be greater than or equal to zero, but was $selectionIndex" }

        previousSearch = searchQuery

        SearchService.searchQuery = searchQuery
        SearchService.selectionIndex = selectionIndex
        SearchService.restart()
    }

    fun textFieldSearchOnAction() {
        if (textFieldSearch.text.isBlank()) return
        if (textFieldSearch.text == previousSearch) return

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
        if (textFieldSearch.text.isBlank()) return
        if (textFieldSearch.text == previousSearch) return

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
            put(
                KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN),
                Runnable { textFieldSearch.requestFocus() }
            )
            put(KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), Runnable { buttonBack.fire() })
            put(KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN), Runnable { buttonNext.fire() })
        }

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED) {
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
        }
    }

    fun onMenuItemNewDefinitionAction() {
        println("TODO")
    }

    fun showSelectedEntryContextMenu(contextMenuEvent: ContextMenuEvent) {
        selectedEntryContextMenu.show(
            flowPaneSelectedEntry.scene.window,
            contextMenuEvent.screenX,
            contextMenuEvent.screenY
        )
    }
}
