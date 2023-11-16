package com.marvinelsen.willow.ui.tasks

import com.marvinelsen.willow.service.CedictService
import com.marvinelsen.willow.service.objects.Word
import javafx.concurrent.Task

class WordsContainingTask(private val word: Word) : Task<List<Word>>() {
    override fun call() = CedictService.findWordsContaining(word)
}