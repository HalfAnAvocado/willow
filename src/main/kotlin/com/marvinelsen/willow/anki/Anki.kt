package com.marvinelsen.willow.anki

import com.marvinelsen.willow.anki.requests.AddNoteParams
import com.marvinelsen.willow.anki.requests.AddNoteRequest
import com.marvinelsen.willow.anki.requests.Note
import com.marvinelsen.willow.config.AnkiConfig
import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Anki(private val config: AnkiConfig) {
    private val client = HttpClient(Java) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun addNoteFor(entry: Entry, definitionSource: SourceDictionary, exampleSentence: String = "") {
        val request = AddNoteRequest(
            params = AddNoteParams(
                Note(
                    deckName = config.deckName,
                    modelName = config.modelName,
                    fieldsContents = mapOf(
                        config.fields.traditional to entry.traditional,
                        config.fields.zhuyin to entry.zhuyin,
                        config.fields.definition to entry.definitions[definitionSource]!!.joinToString(separator = "<br>") { it.htmlDefinition },
                        config.fields.exampleSentence to exampleSentence
                    )
                )
            )
        )

        println(Json.encodeToString<AddNoteRequest>(request))

        val response = client.post(config.ankiConnectUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        println(response.bodyAsText())
    }
}