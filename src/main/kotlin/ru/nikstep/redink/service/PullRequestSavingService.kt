package ru.nikstep.redink.service

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpPost
import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.repo.PullRequestRepository
import ru.nikstep.redink.repo.RepositoryRepository
import java.lang.String.format
import java.nio.charset.Charset


class PullRequestSavingService(
    val pullRequestRepository: PullRequestRepository,
    val repositoryRepository: RepositoryRepository,
    val sourceCodeService: SourceCodeService,
    val githubAppService: GithubAppService,
    val analysisResultService: AnalysisResultService
) {

    val graphqlApi = "https://api.github.com/graphql"
    val fileLinkPattern =
        "{\"query\": \"query {repository(name: \\\"%s\\\", owner: \\\"%s\\\") {object(expression: \\\"%s:%s\\\") {... on Blob{text}}}}\"}"

    @Synchronized
    fun storePullRequest(payload: String) {
        val jsonPayload = JSONObject(payload)

        val installationId = jsonPayload.getJSONObject("installation").getInt("id")

        val pullRequest = jsonPayload.getJSONObject("pull_request")
        val creatorName = pullRequest.getJSONObject("user").getString("login")

        val branchName = pullRequest.getJSONObject("head").getString("ref")
        val headSha = pullRequest.getJSONObject("head").getString("sha")
        val repo = jsonPayload.getJSONObject("repository")
        val repoName = repo.getString("name")
        val fullRepoName = repo.getString("full_name")
        val repoOwner = repo.getJSONObject("owner").getString("login")

        val filePatterns = repositoryRepository.findByName(fullRepoName).filePatterns

        for (fileName in filePatterns) {
            val fileResponse = graphqlApi.httpPost()
                .header("Authorization" to githubAppService.getAccessToken(installationId))
                .body(format(fileLinkPattern, repoName, repoOwner, branchName, fileName))
                .responseObject(JsonObjectDeserializer()).third.get()
            val fileData =
                fileResponse.getJSONObject("data").getJSONObject("repository").getJSONObject("object").getString("text")
            sourceCodeService.save(creatorName, fullRepoName, fileName, fileData)
            analysisResultService.send(installationId, fullRepoName, headSha)
        }
    }

    class JsonObjectDeserializer(private val charset: Charset = Charsets.UTF_8) : ResponseDeserializable<JSONObject> {
        override fun deserialize(response: Response): JSONObject = JSONObject(String(response.data, charset))
    }

}