package ru.nikstep.redink.service

import com.google.gson.Gson
import mu.KotlinLogging
import ru.nikstep.redink.data.AnalysisResultData
import ru.nikstep.redink.data.GithubAnalysisStatus
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.util.RequestUtil
import ru.nikstep.redink.util.asIsoString
import java.util.*

class AnalysisResultService(private val githubAppService: GithubAppService) {
    private val logger = KotlinLogging.logger {}

    fun send(prData: PullRequestData, analysisData: AnalysisResultData) {
        val accessToken = githubAppService.getAccessToken(prData.installationId)
        val body = createBody(prData, analysisData, accessToken)
        RequestUtil.sendStatusCheckRequest(prData.repoFullName, accessToken, body)
        logger.info { "AnalysisResult: sent for ${prData.repoFullName}, creator ${prData.creatorName}" }
    }

    private fun createBody(prData: PullRequestData, analysisData: AnalysisResultData, accessToken: String): String {

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