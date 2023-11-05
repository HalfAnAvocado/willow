package com.marvinelsen.willow.service.objects

import com.marvinelsen.willow.persistence.cedict.WordEntity


data class Word(val traditional: String, val simplified: String, val definitions: List<Definition>)

fun WordEntity.asWord() =
    Word(traditional = traditional, simplified = simplified, definitions = definitions.map { it.asDefinition() })
