package com.marvinelsen.willow.sources.lac

import com.marvinelsen.willow.sources.common.DefinitionFormatter
import kotlinx.html.div
import kotlinx.html.li
import kotlinx.html.ol
import kotlinx.html.span
import kotlinx.html.stream.createHTML

object LacDefinitionFormatter : DefinitionFormatter<LacEntry> {
    override fun formatHtmlDefinition(entry: LacEntry) = createHTML().div {
        ol {
            entry.definitions.forEach { definition ->
                li {
                    span(classes = "definition") {
                        +definition.sanitizeDefinition()
                    }
                    if ("[例]" in definition) {
                        val examples = definition
                            .substringAfter("[例]")
                            .sanitizeExamplesFor(entry.traditional)
                            .split("｜")

                        span(classes = "example") {
                            +examples.joinToString(
                                prefix = "如：",
                                separator = "、",
                                postfix = "。"
                            ) { "「$it」" }
                        }
                    }
                }
            }
        }
    }

    override fun formatShortDefinition(entry: LacEntry) = entry.definitions.first().sanitizeDefinition()

    private fun String.sanitizeDefinition() = this
        .substringAfter('.')
        .substringBefore("[例]")
        .replace("★", "【大陸】")
        .replace("▲", "【臺灣】")

    private fun String.sanitizeExamplesFor(traditional: String) = this
        .replace("～", traditional)
        .replace("★", "【大陸】")
        .replace("▲", "【臺灣】")
        .replace("。", "")
}