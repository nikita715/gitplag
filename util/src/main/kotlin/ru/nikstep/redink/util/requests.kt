package ru.nikstep.redink.util

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import mu.KotlinLogging

const val authorization = "Authorization"
private const val accept = "Accept"
private const val githubGraphqlApi = "https://api.github.com/graphql"
private const val githubAcceptMachineManPreview = "application/vnd.github.machine-man-preview+json"
private const val githubAcceptAntiopePreview = "application/vnd.github.antiope-preview+json"

private val logger = KotlinLogging.logger {}

/**
 * Send a github access token request and get [JsonObject]
 */
fun sendGithubAccessTokenRequest(installationId: String, token: String): JsonObject =
    "https://api.github.com/app/installations/$installationId/access_tokens"
        .httpPost()
        .header(
            authorization to token,
            accept to githubAcceptMachineManPreview
        )
        .send(JsonObjectDeserializer)

/**
 * Send a github status check request and get [JsonObject]
 */
fun sendGithubStatusCheckRequest(repo: String, accessToken: String, body: String) =
    "https://api.github.com/repos/$repo/check-runs"
        .httpPost()
        .header(
            authorization to accessToken,
            accept to githubAcceptAntiopePreview
        )
        .body(body)
        .send(JsonObjectDeserializer)

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
