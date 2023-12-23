package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.ui.controllers.MainController
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.util.Callback

class SentenceCellFactory(private val controller: MainController) : Callback<ListView<Sentence?>, ListCell<Sentence?>> {

    override fun call(listView: ListView<Sentence?>): ListCell<Sentence?> {
        val sentenceCell = SentenceCell(controller)
        sentenceCell.prefWidthProperty().bind(listView.widthProperty().subtract(CELL_PADDING))
        return sentenceCell
    }

    companion object {
        private const val CELL_PADDING = 16
    }
}

internal class SentenceCell(controller: MainController) : ListCell<Sentence?>() {
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

            val menuItemCreateAnkiNoteWithSentence = MenuItem("New Anki Note with Sentence...").apply {
                onAction = EventHandler { controller.onMenuItemNewAnkiNoteWithSentence(item) }
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
