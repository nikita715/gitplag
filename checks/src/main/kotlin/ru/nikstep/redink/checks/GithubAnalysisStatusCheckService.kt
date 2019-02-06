package ru.nikstep.redink.checks

import com.google.gson.Gson
import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.RequestUtil.Companion.sendStatusCheckRequest
import ru.nikstep.redink.util.asIsoString
import ru.nikstep.redink.util.auth.AuthorizationService
import java.util.*

class GithubAnalysisStatusCheckService(private val authorizationService: AuthorizationService) :
    AnalysisStatusCheckService {
    private val logger = KotlinLogging.logger {}

    override fun send(prData: PullRequest, analysisData: AnalysisResultData) {
        val accessToken = authorizationService.getAuthorizationToken(prData.installationId)
        val body = createBody(prData, analysisData)
        sendStatusCheckRequest(prData.repoFullName, accessToken, body)
        logger.info { "AnalysisResult: sent for ${prData.repoFullName}, creator ${prData.creatorName}" }
    }

    private fun createBody(
        prData: PullRequest,
        analysisData: AnalysisResultData
    ): String {

        val body = mutableMapOf<String, Any?>(
            "name" to "Plagiarism tests",
            "head_sha" to prData.headSha,
            "status" to analysisData.status
        )

        if (analysisData.status == GithubAnalysisStatus.COMPLETED.value) {
            body.putAll(
                mapOf(
                    "conclusion" to analysisData.conclusion,
                    "completed_at" to Date().asIsoString(),
                    "details_url" to analysisData.detailsUrl,
                    "output" to mapOf(
                        "title" to "Report",
                        "summary" to analysisData.summary
                    )
                )
            )
        }

        return Gson().toJson(body)
    }
}