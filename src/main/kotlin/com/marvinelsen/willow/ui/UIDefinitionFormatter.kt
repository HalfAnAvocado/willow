package com.marvinelsen.willow.ui

import com.marvinelsen.willow.dictionary.Entry
import com.marvinelsen.willow.dictionary.SourceDictionary

object UIDefinitionFormatter {
    fun format(entry: Entry) = buildList {
        for ((sourceDictionary, definitions) in entry.definitionsByDictionary.entries.sortedBy { it.key }) {
            when (sourceDictionary) {
                SourceDictionary.USER -> add(
                    definitions.joinToString(
                        prefix = "<h1>USER</h1>",
                        separator = "<hr class=\"in-definition\">"
                    ) { it.htmlDefinition }
                )

                SourceDictionary.LAC -> add(
                    definitions.joinToString(
                        prefix = "<h1>LAC</h1>",
                        separator = "<hr class=\"in-definition\">"
                    ) { it.htmlDefinition }
                )

                SourceDictionary.MOE -> add(
                    definitions.joinToString(
                        prefix = "<h1>MoE</h1>",
                        separator = "<hr class=\"in-definition\">"
                    ) { it.htmlDefinition }
                )

                SourceDictionary.CEDICT -> add(
                    definitions.joinToString(
                        prefix = "<h1>CC-CEDICT</h1>",
                        separator = "<hr class=\"in-definition\">"
                    ) { it.htmlDefinition }
                )
            }
        }
    }.joinToString(separator = "<hr>")
}
