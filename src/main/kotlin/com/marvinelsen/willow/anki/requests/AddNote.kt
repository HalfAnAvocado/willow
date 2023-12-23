
package com.marvinelsen.willow.anki.requests

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
class AddNoteRequest(
    @EncodeDefault
    override val action: String = "addNote",
    override val params: AddNoteParams,
) : AnkiConnectRequest<AddNoteParams>()

@Serializable
data class AddNoteParams(val note: Note) : Params()
