package com.marvinelsen.willow.sources.cedict

import com.marvinelsen.willow.sources.common.DefinitionFormatter
import kotlinx.html.div
import kotlinx.html.li
import kotlinx.html.ol
import kotlinx.html.stream.createHTML

object CedictDefinitionFormatter : DefinitionFormatter<CedictEntry> {
    override fun formatHtmlDefinition(entry: CedictEntry) =
        createHTML(prettyPrint = false).div(classes = "cedict-definition") {
            ol {
                entry.definition
                    .split("/")
                    .filterNot { "Taiwan pr. " in it }
                    .forEach {
                        li {
                            +it
                        }
                    }
            }
        }

    override fun formatShortDefinition(entry: CedictEntry) = entry.definition.replace("/", " / ")
}