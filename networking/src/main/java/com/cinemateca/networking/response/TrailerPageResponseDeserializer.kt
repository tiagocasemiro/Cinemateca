package com.cinemateca.networking.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class TrailerPageResponseDeserializer : JsonDeserializer<TrailerPageResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): TrailerPageResponse {
        val response = json.asJsonObject
        val metadata = context.deserialize<PageMetadataResponse>(
            response.get("_metadata"),
            PageMetadataResponse::class.java,
        )
        val items = response.entrySet()
            .asSequence()
            .filter { (key) -> key != "_metadata" }
            .sortedBy { (key) -> key.toIntOrNull() ?: Int.MAX_VALUE }
            .map { (_, value) ->
                context.deserialize<TrailerResponse>(value, TrailerResponse::class.java)
            }
            .toList()

        return TrailerPageResponse(items = items, metadata = metadata)
    }
}
