package com.marvinelsen.willow.ui.services

import com.marvinelsen.willow.dictionary.Dictionary
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.util.task
import javafx.concurrent.Service

object SearchService : Service<SearchResult>() {
    var selectionIndex = 0
    var searchQuery = ""

    override fun createTask() = task {
        SearchResult(
            selectionIndex = selectionIndex,
            entries = Dictionary.search(searchQuery)
        )
    }
}

data class SearchResult(
    val selectionIndex: Int = 0,
    val entries: List<Entry> = emptyList(),
)

object FindCharactersService : Service<List<Entry>>() {
    var selectedEntry: Entry? = null

    override fun createTask() = task {
        if (selectedEntry == null) return@task emptyList()

        Dictionary.findCharactersOf(selectedEntry!!)
    }
}

object FindEntriesContainingService : Service<List<Entry>>() {
    var selectedEntry: Entry? = null

    override fun createTask() = task {
        if (selectedEntry == null) return@task emptyList()

        Dictionary.findEntriesContaining(selectedEntry!!)
    }
}

object FindSentencesService : Service<List<Sentence>>() {
    var selectedEntry: Entry? = null

    override fun createTask() = task {
        if (selectedEntry == null) return@task emptyList()

        Dictionary.findSentencesFor(selectedEntry!!)
    }
}

object AddUserSentenceService : Service<Unit>() {
    var userSentence: Sentence? = null

    override fun createTask() = task {
        if (userSentence == null) return@task

        Dictionary.addUserSentence(userSentence!!)
    }
}

object AddUserEntryService : Service<Unit>() {
    var userEntry: Entry? = null

    override fun createTask() = task {
        if (userEntry == null) return@task

        Dictionary.addUserEntry(userEntry!!)
    }
}
