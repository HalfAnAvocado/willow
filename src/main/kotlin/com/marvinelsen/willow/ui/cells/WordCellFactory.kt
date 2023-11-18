package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.dictionary.objects.Word
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.util.Callback

class WordCellFactory : Callback<ListView<Word?>, ListCell<Word?>> {
    override fun call(listView: ListView<Word?>): ListCell<Word?> {
        val wordCell = WordCell()
        wordCell.prefWidthProperty().bind(listView.widthProperty().subtract(16))
        return wordCell
    }
}

internal class WordCell : ListCell<Word?>() {
    private val labelHeadword = Label().apply {
        styleClass.add("list-view-word")
    }

    private val labelDefinition = Label().apply {
        styleClass.add("list-view-definition")
    }

    private val labelPronunciation = Label().apply {
        styleClass.add("list-view-pronunciation")
    }

    private val flowPane = FlowPane(labelHeadword, labelPronunciation).apply {
        hgap = 8.0
        rowValignment = VPos.BASELINE
    }

    private val root = VBox(flowPane, labelDefinition)

    init {
        text = null
    }

    override fun updateItem(word: Word?, empty: Boolean) {
        super.updateItem(word, empty)
        if (empty || word == null) {
            graphic = null
        } else {
            labelHeadword.text = word.traditional

            val definition = word.preferredDefinitions.first()
            labelPronunciation.text = word.zhuyin
            labelDefinition.text = definition.shortDefinition

            graphic = root
        }
    }
}