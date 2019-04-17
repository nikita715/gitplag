package io.gitplag.util

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable

/**
 * [String] deserializer
 */
object StringDeserializer : ResponseDeserializable<String> {
    override fun deserialize(response: Response): String = String(response.data, Charsets.UTF_8)
}

/**
 * [JsonObject] deserializer
 */
object JsonObjectDeserializer : ResponseDeserializable<JsonObject> {
    override fun deserialize(response: Response): JsonObject = String(response.data, Charsets.UTF_8).parseAsObject()
}

/**
 * [JsonArray] deserializer
 */
object JsonArrayDeserializer : ResponseDeserializable<JsonArray<*>> {
    override fun deserialize(response: Response): JsonArray<*> = String(response.data, Charsets.UTF_8).parseAsArray()
}