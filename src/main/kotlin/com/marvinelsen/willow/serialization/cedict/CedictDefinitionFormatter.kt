package com.marvinelsen.willow.serialization.cedict

object CedictDefinitionFormatter {
    fun formatHtmlDefinition(cedictEntry: CedictEntry) = buildString {
        val definitions = cedictEntry.definition
            .split("/")
            .filterNot { it.contains("Taiwan pr. ") }

        if (definitions.size == 1) {
            append(definitions.first())
        } else {
            append("<ol>")
            definitions.forEach { definition ->
                append("<li>")
                append(definition)
                append("</li>")
            }
            append("</ol>")
        }
    }

    fun formatShortDefinition(cedictEntry: CedictEntry) = cedictEntry.definition.replace("/", " / ")
}