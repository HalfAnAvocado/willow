package com.marvinelsen.willow.sources.lac

import com.marvinelsen.willow.sources.common.DefinitionFormatter

object LacDefinitionFormatter : DefinitionFormatter<LacEntry> {
    override fun formatHtmlDefinition(entry: LacEntry) = buildString {
        if (entry.definitions.size == 1) {
            append(formatDefinition(entry.definitions.first(), entry.traditional))
        } else {
            append("<ol>")
            entry.definitions.forEach { definition ->
                append("<li>")
                append(formatDefinition(definition, entry.traditional))
                append("</li>")
            }
            append("</ol>")
        }
    }

    override fun formatShortDefinition(entry: LacEntry) =
        entry.definitions.first()
            .substringAfter('.')
            .substringBefore("[例]")
            .replace("★", "【大陸】")
            .replace("▲", "【臺灣】")

    private fun formatDefinition(definition: String, traditional: String) = buildString {
        val formattedDefinition = definition.substringAfter('.')
            .replace("～", "<span class=\"headword\">${traditional}</span>")
            .replace("★", "【大陸】")
            .replace("▲", "【臺灣】")

        val examples = if (formattedDefinition.indexOf("[例]") > -1) formattedDefinition.substringAfter("[例]")
            .replace("。", "")
            .split("｜") else null

        append("<span class=\"definition\">${formattedDefinition.substringBefore("[例]")}</span>")
        if (examples != null) {
            append(examples.joinToString(
                prefix = "<span class=\"example\">如：",
                separator = "、",
                postfix = "。</span>"
            ) { "「$it」" })
        }
    }
}