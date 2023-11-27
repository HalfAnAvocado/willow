package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.ui.controllers.MainController
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.input.Clipboard
import javafx.scene.layout.VBox
import javafx.util.Callback

class SentenceCellFactory(private val controller: MainController) : Callback<ListView<Sentence?>, ListCell<Sentence?>> {
    override fun call(listView: ListView<Sentence?>): ListCell<Sentence?> {
        val sentenceCell = SentenceCell(controller)
        sentenceCell.prefWidthProperty().bind(listView.widthProperty().subtract(16))
        return sentenceCell
    }
}

internal class SentenceCell(controller: MainController) : ListCell<Sentence?>() {
    private val systemClipboard = Clipboard.getSystemClipboard()

    private val labelTraditional = Label().apply {
        styleClass.add("chinese")
        styleClass.add("list-view-sentence-cell")
    }
    private val root = VBox(labelTraditional)

    init {
        text = null
        contextMenu = ContextMenu().apply {
            val menuItemCopySentence = MenuItem("Copy Sentence").apply {
                onAction = EventHandler { controller.onMenuItemCopySentence(item) }
            }

            val menuItemCreateAnkiNoteWithSentence = MenuItem("Create Anki Note with Sentence...").apply {
                onAction = EventHandler { controller.onMenuItemCreateAnkiNoteWithSentence(item) }
            }

            items.addAll(menuItemCopySentence, menuItemCreateAnkiNoteWithSentence)
        }
    }

    override fun updateItem(sentence: Sentence?, empty: Boolean) {
        super.updateItem(sentence, empty)
        if (empty || sentence == null) {
            graphic = null
        } else {
            labelTraditional.text = sentence.traditional

            graphic = root
        }
    }

}