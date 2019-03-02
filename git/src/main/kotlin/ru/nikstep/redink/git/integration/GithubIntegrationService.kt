package ru.nikstep.redink.git.integration

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.git.newUser
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.User
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository
import ru.nikstep.redink.util.Language.TEXT
import ru.nikstep.redink.util.parseAsObject

class GithubIntegrationService(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) : IntegrationService {

    private val logger = KotlinLogging.logger {}

    override fun createNewUser(payload: String) {
        val jsonPayload = payload.parseAsObject()

        if (actionIsCreated(jsonPayload)) {
            saveNewUser(jsonPayload).also {
                saveRepositoriesOfTheUser(jsonPayload, it)
            }.also(logger::newUser)
        }
    }

    private fun saveRepositoriesOfTheUser(
        jsonPayload: JsonObject,
        user: User
    ) {
        jsonPayload.array<JsonObject>("repositories")!!.map { repo ->
            Repository(
                language = TEXT,
                owner = user,
                name = repo.string("full_name")!!,
                repoGithubId = repo.long("id")!!
            )
        }.let { repositoryRepository.saveAll(it) }
    }

    private fun saveNewUser(jsonPayload: JsonObject): User {
        val installation = jsonPayload.obj("installation")!!
        val account = installation.obj("account")!!

        val user = User(
            name = account.string("login")!!,
            githubId = account.long("id")!!,
            installationId = installation.long("id")!!
        )

        userRepository.save(user)
        return user
    }

    private fun actionIsCreated(jsonPayload: JsonObject) =
        jsonPayload["action"] == "created"

}