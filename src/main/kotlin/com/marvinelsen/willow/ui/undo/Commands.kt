package com.marvinelsen.willow.ui.undo

import com.marvinelsen.willow.ui.controllers.MainController

interface Command {
    fun execute()
    fun undo()
    fun redo() = execute()
}

class SearchCommand(
    private val mainController: MainController,
    private val oldSelectionIndex: Int,
    private val oldSearchQuery: String,
    private val newSearchQuery: String,
) : Command {
    override fun execute() {
        mainController.search(newSearchQuery)
    }

    override fun undo() {
        mainController.textFieldSearch.text = oldSearchQuery
        mainController.search(oldSearchQuery, oldSelectionIndex)
    }

    override fun redo() {
        mainController.textFieldSearch.text = newSearchQuery
        mainController.search(newSearchQuery)
    }
}