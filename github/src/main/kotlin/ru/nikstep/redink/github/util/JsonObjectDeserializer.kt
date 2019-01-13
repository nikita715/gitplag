package ru.nikstep.redink.github.util

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import org.springframework.boot.configurationprocessor.json.JSONObject
import java.nio.charset.Charset

class JsonObjectDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JSONObject> {
    override fun deserialize(response: Response): JSONObject = JSONObject(String(response.data, charset))
}