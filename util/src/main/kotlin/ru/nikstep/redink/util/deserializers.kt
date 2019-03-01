package ru.nikstep.redink.util

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable

object StringDeserializer : ResponseDeserializable<String> {
    override fun deserialize(response: Response): String = String(response.data, Charsets.UTF_8)
}

object JsonObjectDeserializer : ResponseDeserializable<JsonObject> {
    override fun deserialize(response: Response): JsonObject = String(response.data, Charsets.UTF_8).parseAsObject()
}

object JsonArrayDeserializer : ResponseDeserializable<JsonArray<*>> {
    override fun deserialize(response: Response): JsonArray<*> = String(response.data, Charsets.UTF_8).parseAsArray()
}