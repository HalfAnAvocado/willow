package com.marvinelsen.willow.dictionary

import com.marvinelsen.willow.dictionary.objects.Entry
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

    fun shutdownExecutor() {
        databaseExecutor.shutdown()
    }
}