package com.marvinelsen.willow.sources.common

interface DefinitionFormatter<T> {
    fun formatHtmlDefinition(entry: T): String
    fun formatShortDefinition(entry: T): String
}
