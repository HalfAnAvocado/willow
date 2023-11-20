package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.dictionary.objects.Entry
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.util.Callback

class EntryCellFactory : Callback<ListView<Entry?>, ListCell<Entry?>> {
    override fun call(listView: ListView<Entry?>): ListCell<Entry?> {
        val entryCell = EntryCell()
        entryCell.prefWidthProperty().bind(listView.widthProperty().subtract(16))
        return entryCell
    }
}

internal class EntryCell : ListCell<Entry?>() {
    private val labelHeadword = Label().apply {
        styleClass.add("list-view-entry")
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

    override fun updateItem(entry: Entry?, empty: Boolean) {
        super.updateItem(entry, empty)
        if (empty || entry == null) {
            graphic = null
        } else {
            labelHeadword.text = entry.traditional

            val definition = entry.preferredDefinitions.first()
            labelPronunciation.text = entry.zhuyin
            labelDefinition.text = definition.shortDefinition

            graphic = root
        }
    }
}