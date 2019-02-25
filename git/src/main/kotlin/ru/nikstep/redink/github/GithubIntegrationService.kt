package ru.nikstep.redink.github

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.User
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository

class GithubIntegrationService(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) : IntegrationService {

    private val logger = KotlinLogging.logger {}

    override fun createNewUser(payload: String) {
        val jsonPayload = JSONObject(payload)

        if (actionIsCreated(jsonPayload)) {
            val user = saveNewUser(jsonPayload)
            saveRepositoriesOfTheUser(jsonPayload, user)
            logger.info { "Webhook: Integration: registered new user ${user.name}, github id ${user.githubId}" }
        }
    }

    private fun saveRepositoriesOfTheUser(
        jsonPayload: JSONObject,
        user: User
    ) {
        val repositories = jsonPayload.getJSONArray("repositories")

        for (i in 0 until repositories.length()) {
            val jsonRepository = repositories.getJSONObject(i)
            repositoryRepository.save(
                Repository(
                    owner = user,
                    name = jsonRepository.getString("full_name"),
                    repoGithubId = jsonRepository.getLong("id")
                )
            )
        }
    }

    private fun saveNewUser(jsonPayload: JSONObject): User {
        val installation = jsonPayload.getJSONObject("installation")
        val account = installation.getJSONObject("account")

        val user = User(
            name = account.getString("login"),
            githubId = account.getLong("id"),
            installationId = installation.getLong("id")
        )

        userRepository.save(user)
        return user
    }

    private fun actionIsCreated(jsonPayload: JSONObject) =
        jsonPayload.getString("action") == "created"

}