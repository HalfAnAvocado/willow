package com.marvinelsen.willow.sources.moe

import kotlinx.html.div
import kotlinx.html.li
import kotlinx.html.ol
import kotlinx.html.span
import kotlinx.html.stream.createHTML

// TODO: Make this inherit DefinitionFormatter
object MoeDefinitionFormatter {
    fun formatHtmlDefinition(moeDefinitions: List<MoeDefinition>) =
        createHTML(prettyPrint = false).div(classes = "moe-definition") {
            moeDefinitions.groupBy { it.type ?: "" }.entries.forEach { (type, definitions) ->
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

    fun formatShortDefinition(moeDefinition: MoeDefinition) = moeDefinition.content
}