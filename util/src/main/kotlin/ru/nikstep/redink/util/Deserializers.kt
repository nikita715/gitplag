package ru.nikstep.redink.util

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import java.nio.charset.Charset

class StringDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<String> {
    override fun deserialize(response: Response): String = String(response.data, charset)
}

class JsonObjectDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JSONObject> {
    override fun deserialize(response: Response): JSONObject = JSONObject(String(response.data, charset))
}

class JsonArrayDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JSONArray> {
    override fun deserialize(response: Response): JSONArray = JSONArray(String(response.data, charset))
}