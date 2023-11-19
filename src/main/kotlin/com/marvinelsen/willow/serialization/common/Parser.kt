package com.marvinelsen.willow.serialization.common

import java.io.InputStream

interface Parser<T> {
    fun parse(inputStream: InputStream): List<T>
}