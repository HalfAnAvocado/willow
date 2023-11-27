package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.dictionary.database.SentenceEntity
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javafx.concurrent.Task

object AsyncDictionary {
    private val databaseExecutor = Executors.newSingleThreadExecutor()

    fun search(query: String, onSucceeded: (List<Entry>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Entry>>() {
            override fun call() = Dictionary.search(query)
        }.apply { setOnSucceeded { onSucceeded(value) } })

    fun findEntriesContaining(entry: Entry, onSucceeded: (List<Entry>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Entry>>() {
            override fun call() = Dictionary.findEntriesContaining(entry)
        }.apply { setOnSucceeded { onSucceeded(value) } })

    fun findCharactersOf(entry: Entry, onSucceeded: (List<Entry>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Entry>>() {
            override fun call() = Dictionary.findCharactersOf(entry)
        }.apply { setOnSucceeded { onSucceeded(value) } })


    fun findSentencesFor(entry: Entry, onSucceeded: (List<Sentence>) -> Unit): Future<*> =
        databaseExecutor.submit(object : Task<List<Sentence>>() {
            override fun call() = Dictionary.findSentencesFor(entry)
        }.apply { setOnSucceeded { onSucceeded(value) } })

    fun addUserSentence(sentence: Sentence): Future<*> =
        databaseExecutor.submit(object : Task<SentenceEntity>() {
            override fun call() = Dictionary.addUserSentence(sentence)
        })

    fun shutdownExecutor() {
        databaseExecutor.shutdown()
    }
}