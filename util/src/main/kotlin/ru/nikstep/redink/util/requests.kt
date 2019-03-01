package ru.nikstep.redink.util

import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import mu.KotlinLogging

private const val authorization = "Authorization"
private const val accept = "Accept"
private const val githubGraphqlApi = "https://api.github.com/graphql"
private const val githubAcceptMachineManPreview = "application/vnd.github.machine-man-preview+json"
private const val githubAcceptAntiopePreview = "application/vnd.github.antiope-preview+json"

private val logger = KotlinLogging.logger {}

fun sendGithubAccessTokenRequest(installationId: Int, token: String): JsonObject =
    "https://api.github.com/app/installations/$installationId/access_tokens"
        .httpPost()
        .header(
            authorization to token,
            accept to githubAcceptMachineManPreview
        )
        .send(JsonObjectDeserializer)

fun sendGithubStatusCheckRequest(repo: String, accessToken: String, body: String) =
    "https://api.github.com/repos/$repo/check-runs"
        .httpPost()
        .header(
            authorization to accessToken,
            accept to githubAcceptAntiopePreview
        )
        .body(body)
        .send(JsonObjectDeserializer)

fun sendGithubGraphqlRequest(
    body: String,
    accessToken: String,
    deserializer: JsonObjectDeserializer = JsonObjectDeserializer,
    method: Method = Method.GET
): JsonObject =
    githubGraphqlApi
        .toRequest(method)
        .header(authorization to accessToken)
        .body(body)
        .send(deserializer)

fun <T : Any> sendRestRequest(
    url: String,
    body: String = "",
    accessToken: String = "",
    deserializer: ResponseDeserializable<T>,
    method: Method = Method.GET
): T =
    url.toRequest(method)
        .header(authorization to accessToken)
        .body(body).send(deserializer)

private fun <T : Any> Request.send(deserializer: ResponseDeserializable<T>): T =
    logger.logged(request) {
        request.responseObject(deserializer)
    }.third.get()

private fun String.toRequest(method: Method): Request {
    return when (method) {
        Method.POST -> this.httpPost()
        else -> this.httpGet()
    }
}