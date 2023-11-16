package com.marvinelsen.willow.ui.tasks

import com.marvinelsen.willow.service.CedictService
import com.marvinelsen.willow.service.objects.Word
import javafx.concurrent.Task

class SearchTask(private val query: String) : Task<List<Word>>() {
    override fun call() = CedictService.search(query)
}
