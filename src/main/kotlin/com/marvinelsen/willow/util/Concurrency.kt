package com.marvinelsen.willow.util

import javafx.concurrent.Task

fun <T> task(block: () -> T) = object : Task<T>() {
    override fun call() = block()
}