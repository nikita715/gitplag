package ru.nikstep.redink.github

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.Git.BITBUCKET
import ru.nikstep.redink.util.JsonArrayDeserializer
import ru.nikstep.redink.util.RequestUtil.Companion.sendRestRequest

class BitbucketPullRequestWebhookService(private val pullRequestRepository: PullRequestRepository) : WebhookService {

    private val logger = KotlinLogging.logger {}

    override fun saveNewPullRequest(payload: String) {
        //https://api.bitbucket.org/1.0/repositories/nikita715/plagiarism_test2/changesets/e56f83a49b7692ba482cf1556661dcaf2b7b9140/diffstat

        val jsonPayload = JSONObject(payload)

        val pullRequestJson = jsonPayload.getJSONObject("pullrequest")

        val headSha = pullRequestJson.getJSONObject("source").getJSONObject("commit").getString("hash")
        val repoFullName =
            pullRequestJson.getJSONObject("destination").getJSONObject("repository").getString("full_name")

        val jsonChangedFiles = (sendRestRequest(
            url = "https://api.bitbucket.org/1.0/repositories/$repoFullName/changesets/$headSha/diffstat",
            deserializer = JsonArrayDeserializer()
        ) as JSONArray)

        val changedFiles =
            (0 until jsonChangedFiles.length()).map { (jsonChangedFiles.get(it) as JSONObject).getString("file") }

        val pullRequest = PullRequest(
            gitService = BITBUCKET,
            repoId = -1,
            number = pullRequestJson.getInt("id"),
            repoFullName = repoFullName,
            creatorName = pullRequestJson.getJSONObject("author").getString("username"),
            headSha = headSha,
            branchName = pullRequestJson.getJSONObject("source").getJSONObject("branch").getString("name"),
            changedFiles = changedFiles
        )

        logger.info {
            "Webhook: PullRequest: new from repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}," +
                    " branch ${pullRequest.branchName}, url https://github.com/${pullRequest.repoFullName}/pull/${pullRequest.number}"
        }

        pullRequestRepository.save(pullRequest)
    }

}