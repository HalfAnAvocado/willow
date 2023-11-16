package com.marvinelsen.willow.ui.tasks

import com.marvinelsen.willow.dictionary.Dictionary
import com.marvinelsen.willow.dictionary.objects.Word
import javafx.concurrent.Task

class WordsContainingTask(private val word: Word) : Task<List<Word>>() {
    override fun call() = Dictionary.findWordsContaining(word)
}