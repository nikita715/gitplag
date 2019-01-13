package ru.nikstep.redink.github.util

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpMethod

class RequestUtil {

    companion object {
        private const val githubGraphql = "https://api.github.com/graphql"
        private const val acceptMachineManPreview = "application/vnd.github.machine-man-preview+json"
        private const val acceptAntiopePreview = "application/vnd.github.antiope-preview+json"

        private val logger = KotlinLogging.logger {}

        fun sendAccessTokenRequest(installationId: Int, token: String): JSONObject {
            val requestEndpoint = "https://api.github.com/app/installations/$installationId/access_tokens"
            val triple = requestEndpoint.httpPost()
                .header(
                    "Authorization" to token,
                    "Accept" to acceptMachineManPreview
                ).responseObject(JsonObjectDeserializer())
            logger.responseInfo(triple)
            return triple.third.get()
        }

        fun sendStatusCheckRequest(repo: String, accessToken: String, body: String) {
            val requestEndpoint = "https://api.github.com/repos/$repo/check-runs"
            val triple = requestEndpoint.httpPost()
                .header(
                    "Authorization" to accessToken,
                    "Accept" to acceptAntiopePreview
                )
                .body(body).responseObject(JsonObjectDeserializer())
            logger.responseInfo(triple)
        }

        fun sendGraphqlRequest(
            body: String,
            accessToken: String,
            deserializer: JsonObjectDeserializer = JsonObjectDeserializer(),
            httpMethod: HttpMethod = HttpMethod.GET
        ): JSONObject {
            val triple = when (httpMethod) {
                HttpMethod.POST -> githubGraphql.httpPost()
                else -> githubGraphql.httpGet()
            }
                .header("Authorization" to accessToken)
                .body(body)
                .responseObject(deserializer)
            logger.responseInfo(triple)
            return triple.third.get()
        }
    }

}