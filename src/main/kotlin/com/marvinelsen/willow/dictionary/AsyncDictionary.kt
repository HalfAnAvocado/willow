package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.dictionary.objects.Word
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javafx.concurrent.Task

object AsyncDictionary {
    private val databaseExecutor = Executors.newSingleThreadExecutor()

    fun search(query: String, onSucceeded: (List<Word>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Word>>() {
            override fun call() = Dictionary.search(query)
        }.apply { setOnSucceeded { onSucceeded(value) } })

    fun findWordsContaining(word: Word, onSucceeded: (List<Word>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Word>>() {
            override fun call() = Dictionary.findWordsContaining(word)
        }.apply { setOnSucceeded { onSucceeded(value) } })

    fun findCharactersOf(word: Word, onSucceeded: (List<Word>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Word>>() {
            override fun call() = Dictionary.findCharactersOf(word)
        }.apply { setOnSucceeded { onSucceeded(value) } })

    fun shutdownExecutor() {
        databaseExecutor.shutdown()
    }
}