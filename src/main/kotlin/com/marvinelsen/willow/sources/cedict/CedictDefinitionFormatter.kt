package com.marvinelsen.willow.sources.cedict

import com.marvinelsen.willow.sources.common.DefinitionFormatter

object CedictDefinitionFormatter : DefinitionFormatter<CedictEntry> {
    override fun formatHtmlDefinition(entry: CedictEntry) = buildString {
        val definitions = entry.definition
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

    override fun formatShortDefinition(entry: CedictEntry) = entry.definition.replace("/", " / ")
}