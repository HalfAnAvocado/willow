package com.marvinelsen.willow.sources.tatoeba

import com.marvinelsen.willow.dictionary.SentenceSource
import com.marvinelsen.willow.dictionary.database.SentenceEntity
import com.marvinelsen.willow.sources.common.DatabaseImporter
import org.jetbrains.exposed.sql.transactions.transaction

object TatoebaDatabaseImporter : DatabaseImporter<TatoebaSentence> {
    override fun import(entries: List<TatoebaSentence>) {
        transaction {
            entries.forEach {
                SentenceEntity.new {
                    traditional = it.traditional
                    characterCount = it.traditional.length
                    sentenceSource = SentenceSource.TATOEBA
                }
            }
        }
    }
}