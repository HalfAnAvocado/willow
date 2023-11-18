package com.marvinelsen.willow.serialization.cedict

import com.marvinelsen.willow.dictionary.objects.Definition

object CedictDefinitionFormatter {
    fun formatForDatabase(cedictEntry: CedictEntry) = cedictEntry.definition
        .split("/")
        .filterNot { it.contains("Taiwan pr. ") }
        .joinToString(separator = "/")

    fun formatForDisplay(cedictDefinitions: List<Definition>) = buildString {
        append("<h1>CC-CEDICT</h1>")
        append("<ol>")
        cedictDefinitions.forEach { definition ->
            definition.content.split("/").forEach {
                append("<li>")
                append(it)
                append("</li>")
            }
        }
        append("</ol>")
    }
}