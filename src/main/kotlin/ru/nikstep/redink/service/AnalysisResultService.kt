package ru.nikstep.redink.service

import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class AnalysisResultService(val githubAppService: GithubAppService) {

    fun send(installationId: Int, repo: String, sha: String) {
        val jwt = githubAppService.getAccessToken(installationId)

        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        df.timeZone = tz
        val nowAsISO = df.format(Date())

        val body = mapOf<String, Any>(
            "name" to "Plagiarism tests",
            "head_sha" to sha,
            "status" to "completed",
            "conclusion" to "success",
            "completed_at" to nowAsISO,
            "details_url" to "localhost:8080",
            "output" to mapOf<String, Any>(
                "title" to "Mighty Readme report",
                "summary" to "Sample summary"
            )
        )
        val body1 = Gson().toJson(body)

        "https://api.github.com/repos/$repo/check-runs".httpPost()
            .header(
                "Authorization" to jwt,
                "Accept" to "application/vnd.github.antiope-preview+json"
            )
            .body(body1)
            .response()
    }

}