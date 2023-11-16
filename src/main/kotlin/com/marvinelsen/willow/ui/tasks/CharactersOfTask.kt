package com.marvinelsen.willow.ui.tasks

import com.marvinelsen.willow.service.CedictService
import com.marvinelsen.willow.service.objects.Word
import javafx.concurrent.Task

class CharactersOfTask(private val word: Word) : Task<List<Word>>() {
    override fun call() = CedictService.findCharactersOf(word)
}
