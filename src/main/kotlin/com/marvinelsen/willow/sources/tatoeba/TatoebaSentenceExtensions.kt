package com.marvinelsen.willow.sources.tatoeba

import com.marvinelsen.willow.dictionary.Sentence
import com.marvinelsen.willow.dictionary.SentenceSource

fun List<TatoebaSentence>.toSentences() = this.map(TatoebaSentence::toSentence)

fun TatoebaSentence.toSentence() = Sentence(
    traditional = traditional,
    source = SentenceSource.TATOEBA
)
