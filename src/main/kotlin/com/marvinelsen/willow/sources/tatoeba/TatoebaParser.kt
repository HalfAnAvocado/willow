package com.marvinelsen.willow.sources.tatoeba

import com.marvinelsen.willow.sources.common.Parser
import java.io.InputStream

object TatoebaParser : Parser<TatoebaSentence> {
    override fun parse(inputStream: InputStream) =
        inputStream
            .bufferedReader()
            .lineSequence()
            .map { it.toToTatoebaSentence() }
            .toList()
}

private fun String.toToTatoebaSentence() = TatoebaSentence(traditional = this.split('\t')[2])