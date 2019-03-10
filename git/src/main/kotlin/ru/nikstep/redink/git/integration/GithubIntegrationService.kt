package ru.nikstep.redink.git.integration

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.git.newUser
import ru.nikstep.redink.model.data.RepositoryDataManager
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.User
import ru.nikstep.redink.model.repo.UserRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalysisMode
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language.TEXT
import ru.nikstep.redink.util.parseAsObject

/**
 * Service for user/repository integration with github
 */
class GithubIntegrationService(
    private val userRepository: UserRepository,
    private val repositoryDataManager: RepositoryDataManager
) : IntegrationService {

    override fun manageRepositories(payload: String) {
        val jsonPayload = payload.parseAsObject()
        val addedRepositories = jsonPayload.array<JsonObject>("repositories_added")
        if (addedRepositories != null && addedRepositories.size > 0) {
            val repoNames =
                addedRepositories.map { jsonRepository ->
                    requireNotNull(jsonRepository.string("full_name")) { "Repo name is null" }
                }
            repositoryDataManager.create(jsonPayload.obj("sender")?.string("login")!!, GitProperty.GITHUB, repoNames)
        }
        val removedRepositories = jsonPayload.array<JsonObject>("repositories_removed")
        if (removedRepositories != null && removedRepositories.size > 0) {
            val repoNames = removedRepositories.map { jsonRepository ->
                requireNotNull(jsonRepository.string("full_name")) { "Repo name is null" }
            }
            repositoryDataManager.delete(repoNames)
        }
    }

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
        jsonPayload.array<JsonObject>("repositories")?.map { repo ->
            Repository(
                language = TEXT,
                owner = user,
                name = requireNotNull(repo.string("full_name")) { "Repo name is null" },
                analysisMode = AnalysisMode.STATIC,
                gitService = GitProperty.GITHUB,
                analyser = AnalyserProperty.MOSS
            )
        }.let { repositoryDataManager.saveAll(requireNotNull((it)) { "Pull request is null" }) }
    }

    private fun saveNewUser(jsonPayload: JsonObject): User {
        val installation = jsonPayload.obj("installation")
        val account = installation?.obj("account")

        val user = User(
            name = requireNotNull(account?.string("login")) { "Name is null" },
            githubId = requireNotNull(account?.long("id")) { "Github id is null" },
            installationId = requireNotNull(installation?.long("id")) { "InstallationId is null" }
        )

        return userRepository.save(user)
    }

    private fun actionIsCreated(jsonPayload: JsonObject) =
        jsonPayload["action"] == "created"

}