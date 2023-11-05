package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.service.objects.Word
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.util.Callback

class WordCellFactory : Callback<ListView<Word?>, ListCell<Word?>> {
    override fun call(listView: ListView<Word?>): ListCell<Word?> {
        val wordCell = WordCell()
        wordCell.prefWidthProperty().bind(listView.widthProperty().subtract(16))
        return wordCell
    }
}

internal class WordCell : ListCell<Word?>() {
    private val textFlowHeadWord = TextFlow()
    private val labelDefinition = Label()
    private val root = VBox(textFlowHeadWord, labelDefinition)

    override fun updateItem(word: Word?, empty: Boolean) {
        super.updateItem(word, empty)
        if (empty || word == null) {
            text = null
            graphic = null
        } else {
            textFlowHeadWord.children.clear()
            val characters = word.traditional.split("")
            for (i in characters.indices) {
                val characterText = Text(characters.get(i))
                characterText.fill = Color.web("#000")
                characterText.styleClass.add("list-view-word")
                textFlowHeadWord.children.add(characterText)
            }
            labelDefinition.text = word.definitions.first().content
            text = null
            graphic = root
        }
    }
}