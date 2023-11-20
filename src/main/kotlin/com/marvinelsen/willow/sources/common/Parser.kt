package com.marvinelsen.willow.sources.common

import java.io.InputStream

interface Parser<T> {
    fun parse(inputStream: InputStream): List<T>
}