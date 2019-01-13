package ru.nikstep.redink.github.service

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.User
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository

class IntegrationService(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) {

    private val logger = KotlinLogging.logger {}

    fun createNewUser(payload: String) {
        val jsonPayload = JSONObject(payload)

        if (jsonPayload.getString("action") != "created") return

        val installation = jsonPayload.getJSONObject("installation")
        val installationId = installation.getLong("id")
        val account = installation.getJSONObject("account")
        val userLogin = account.getString("login")
        val userId = account.getLong("id")

        val user = User(name = userLogin, githubId = userId, installationId = installationId)

        userRepository.save(user)

        val repositories = jsonPayload.getJSONArray("repositories")

        for (i in 0 until repositories.length()) {
            val jsonRepository = repositories.getJSONObject(i)
            repositoryRepository.save(
                Repository(
                    owner = user,
                    name = jsonRepository.getString("full_name"),
                    githubId = jsonRepository.getLong("id")
                )
            )
        }

    }

}