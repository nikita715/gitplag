package ru.nikstep.redink.github.util

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import org.springframework.boot.configurationprocessor.json.JSONArray
import java.nio.charset.Charset

class JsonArrayDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JSONArray> {
    override fun deserialize(response: Response): JSONArray = JSONArray(String(response.data, charset))
}