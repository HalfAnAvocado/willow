package com.marvinelsen.willow.ui.cells

import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.ui.controllers.MainController
import javafx.event.EventHandler
import javafx.geometry.VPos
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.util.Callback

class EntryCellFactory(private val controller: MainController) : Callback<ListView<Entry?>, ListCell<Entry?>> {
    override fun call(listView: ListView<Entry?>): ListCell<Entry?> {
        val entryCell = EntryCell(controller)
        entryCell.prefWidthProperty().bind(listView.widthProperty().subtract(CELL_PADDING))
        return entryCell
    }

    companion object {
        private const val CELL_PADDING = 16
    }
}

internal class EntryCell(private val controller: MainController) : ListCell<Entry?>() {
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
        hgap = FLOW_PANE_HGAP
        rowValignment = VPos.BASELINE
    }

    private val root = VBox(flowPane, labelDefinition)

    init {
        text = null
        contextMenu = ContextMenu().apply {
            val menuItemCopyHeadword = MenuItem("Copy Headword").apply {
                onAction = EventHandler { controller.copyHeadword(item) }
            }

            val menuItemCopyPronunciation = MenuItem("Copy Pronunciation").apply {
                onAction = EventHandler { controller.copyPronunciation(item) }
            }

            val menuItemNewAnkiNote = MenuItem("New Anki Note...").apply {
                onAction = EventHandler { controller.showNewAnkiNoteDialog(item) }
            }

            items.addAll(menuItemCopyHeadword, menuItemCopyPronunciation, SeparatorMenuItem(), menuItemNewAnkiNote)
        }
    }

    override fun updateItem(entry: Entry?, empty: Boolean) {
        super.updateItem(entry, empty)
        if (empty || entry == null) {
            graphic = null
        } else {
            labelHeadword.text = entry.traditional

            val definition = entry.definitions.entries.minByOrNull { it.key }!!.value.first()
            labelPronunciation.text = entry.accentedPinyin
            labelDefinition.text = definition.shortDefinition

            graphic = root
        }
    }

    companion object {
        private const val FLOW_PANE_HGAP = 8.0
    }
}
