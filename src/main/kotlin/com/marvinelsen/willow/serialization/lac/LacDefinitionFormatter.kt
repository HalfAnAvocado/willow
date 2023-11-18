package com.marvinelsen.willow.serialization.lac

object LacDefinitionFormatter {
    fun formatHtmlDefinition(lacEntry: LacEntry) = buildString {
        if (lacEntry.definitions.size == 1) {
            append(formatDefinition(lacEntry.definitions.first(), lacEntry.traditional))
        } else {
            append("<ol>")
            lacEntry.definitions.forEach { definition ->
                append("<li>")
                append(formatDefinition(definition, lacEntry.traditional))
                append("</li>")
            }
            append("</ol>")
        }
    }

    fun formatShortDefinition(lacEntry: LacEntry) =
        lacEntry.definitions.first().substringAfter('.').substringBefore("[例]")

    private fun formatDefinition(definition: String, traditional: String) = buildString {
        val formattedDefinition = definition.substringAfter('.')
            .replace("～", "<span class=\"headword\">${traditional}</span>")
            .replace("★", "【大陸】")
            .replace("▲", "【臺灣】")

        val examples = if (formattedDefinition.indexOf("[例]") > -1) formattedDefinition.substringAfter("[例]")
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