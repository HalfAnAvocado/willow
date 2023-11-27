package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.dictionary.Sentence
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.util.Callback

class SentenceCellFactory : Callback<ListView<Sentence?>, ListCell<Sentence?>> {
    override fun call(listView: ListView<Sentence?>): ListCell<Sentence?> {
        val sentenceCell = SentenceCell()
        sentenceCell.prefWidthProperty().bind(listView.widthProperty().subtract(16))
        return sentenceCell
    }
}

internal class SentenceCell : ListCell<Sentence?>() {
    private val labelTraditional = Label().apply {
        styleClass.add("list-view-sentence")
    }
    private val root = VBox(labelTraditional)

    init {
        text = null
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