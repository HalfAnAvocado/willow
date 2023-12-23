package com.marvinelsen.willow.sources.lac

data class LacEntry(
    val traditional: String,
    val zhuyinTaiwan: String,
    val zhuyinMainland: String,
    val definitions: List<String>,
)
