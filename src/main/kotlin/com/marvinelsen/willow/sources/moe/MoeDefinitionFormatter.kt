package com.marvinelsen.willow.sources.moe

import com.marvinelsen.willow.sources.common.DefinitionFormatter
import kotlinx.html.div
import kotlinx.html.li
import kotlinx.html.ol
import kotlinx.html.span
import kotlinx.html.stream.createHTML

object MoeDefinitionFormatter : DefinitionFormatter<MoeHeteronym> {
    override fun formatHtmlDefinition(entry: MoeHeteronym) =
        createHTML(prettyPrint = false).div(classes = "moe-definition") {
            entry.definitions.groupBy { it.type ?: "" }.entries.forEach { (type, definitions) ->
                if (type != "") {
                    span(classes = "type") {
                        +type
                    }
                }

                ol {
                    definitions.forEach { definition ->
                        li {
                            span(classes = "definition") {
                                +definition.content
                            }

                            definition.examples.forEach { example ->
                                span(classes = "example") {
                                    +example
                                }
                            }

                            definition.quotes.forEach { quote ->
                                span(classes = "quote") {
                                    +quote
                                }
                            }

                            definition.synonyms?.let {
                                span(classes = "synonyms") {
                                    +"似：${it.replace(",", "、")}"
                                }
                            }

                            definition.antonyms?.let {
                                span(classes = "antonyms") {
                                    +"反：${it.replace(",", "、")}"
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun formatShortDefinition(entry: MoeHeteronym) = entry.definitions.first().content
}
