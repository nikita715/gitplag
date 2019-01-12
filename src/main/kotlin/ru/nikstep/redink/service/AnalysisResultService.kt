package ru.nikstep.redink.service

import com.google.gson.Gson
import mu.KotlinLogging
import ru.nikstep.redink.data.AnalysisResultData
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.util.RequestUtil
import ru.nikstep.redink.util.asIsoString
import java.util.*

class AnalysisResultService(private val githubAppService: GithubAppService) {
    private val logger = KotlinLogging.logger {}

    fun send(prData: PullRequestData, analysisData: AnalysisResultData) {
        val jwt = githubAppService.getAccessToken(prData.installationId)

        val body = Gson().toJson(
            mapOf(
                "name" to "Plagiarism tests",
                "head_sha" to prData.headSha,
                "status" to analysisData.status.value,
                "conclusion" to analysisData.conclusion.value,
                "completed_at" to Date().asIsoString(),
                "details_url" to analysisData.detailsUrl,
                "output" to mapOf(
                    "title" to "Report",
                    "summary" to analysisData.summary
                )
            )
        )

        RequestUtil.sendStatusCheckRequest(prData.repoFullName, jwt, body)

        logger.info { "AnalysisResult: sent for ${prData.repoFullName}, creator ${prData.creatorName}" }
    }

}