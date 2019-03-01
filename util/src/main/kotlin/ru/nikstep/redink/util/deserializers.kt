package ru.nikstep.redink.util

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import java.nio.charset.Charset

class StringDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<String> {
    override fun deserialize(response: Response): String = String(response.data, charset)
}

class JsonObjectDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JsonObject> {
    override fun deserialize(response: Response): JsonObject = String(response.data, charset).parseAsObject()
}

class JsonArrayDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JsonArray<*>> {
    override fun deserialize(response: Response): JsonArray<*> = String(response.data, charset).parseAsArray()
}