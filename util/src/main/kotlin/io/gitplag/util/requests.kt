package io.gitplag.util

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import mu.KotlinLogging

const val authorization = "Authorization"

private val logger = KotlinLogging.logger {}

/**
 * Send a common request and get [T]
 */
inline fun <reified T : Any> sendRestRequest(
    url: String,
    body: String = "",
    accessToken: String = "",
    method: Method = Method.GET
): T =
    url.toRequest(method)
        .header(authorization to accessToken)
        .body(body).send(deserializer<T>()) as T

/**
 * Send a common request and get [T]
 */
fun sendAnalysisResult(
    url: String,
    body: String = "",
    accessToken: String = ""
) {
    val request = url.httpPost().body(body)
    request.headers["Content-Type"] = "application/json"
    request.headers[authorization] = accessToken
    request.response()
}

/**
 * Get [ResponseDeserializable] by [T]
 */
inline fun <reified T : Any> deserializer(): ResponseDeserializable<*> = when (T::class) {
    String::class -> StringDeserializer
    JsonObject::class -> JsonObjectDeserializer
    JsonArray::class -> JsonArrayDeserializer
    else -> JsonObjectDeserializer
}

/**
 * Send [this] and deserialize [T] by the [deserializer]
 */
fun <T : Any> Request.send(deserializer: ResponseDeserializable<T>): T =
    logger.logged(request) {
        request.responseObject(deserializer)
    }.third.get()

/**
 * Transform the [method] to the corresponding [Request]
 */
fun String.toRequest(method: Method): Request = when (method) {
    Method.POST -> this.httpPost()
    else -> this.httpGet()
}
