package com.marvinelsen.willow.sources.common

interface DatabaseImporter<T> {
    fun import(entries: List<T>)
}