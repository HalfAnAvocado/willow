package com.marvinelsen.willow.serialization.moe

import com.marvinelsen.willow.dictionary.objects.Definition

object MoeDefinitionFormatter {
    fun formatHtmlDefinition(moeDefinitions: List<MoeDefinition>) = buildString {
        moeDefinitions.groupBy { it.type ?: "" }.entries.forEach { (type, definitions) ->
            if (type != "") {
                append("<span class=\"type\">$type</span>")
            }
            if (definitions.size == 1) {
                append(formatDefinition(definitions.first()))
            } else {
                append("<ol>")
                definitions.forEach { definition ->
                    append("<li>")
                    append(formatDefinition(definition))
                    append("</li>")
                }
                append("</ol>")
            }
        }
    }

    fun formatShortDefinition(moeDefinition: MoeDefinition) = moeDefinition.content

    private fun formatDefinition(moeDefinition: MoeDefinition) = buildString {
        append("<span class=\"definition\">${moeDefinition.content}</span>")
        moeDefinition.examples.forEach { example ->
            append("<span class=\"example\">$example</span>")
        }
        moeDefinition.quotes.forEach { quote ->
            append("<span class=\"quote\">$quote</span>")
        }
        if (moeDefinition.synonyms != null) {
            append("<span class=\"synonyms\">似：${moeDefinition.synonyms.replace(",", "、")}</span>")
        }
        if (moeDefinition.antonyms != null) {
            append("<span class=\"antonyms\">反：${moeDefinition.antonyms.replace(",", "、")}</span>")
        }
    }
}