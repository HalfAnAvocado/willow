package com.marvinelsen.willow.ui.tasks

import com.marvinelsen.willow.dictionary.Dictionary
import com.marvinelsen.willow.dictionary.objects.Word
import javafx.concurrent.Task

class SearchTask(private val query: String) : Task<List<Word>>() {
    override fun call() = Dictionary.search(query)
}
