package io.gitplag.core.rest

import io.gitplag.core.websocket.NotificationService
import io.gitplag.git.payload.GitManager
import io.gitplag.model.dto.InputRepositoryDto
import io.gitplag.model.dto.OutputRepositoryDto
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/repositories")
class RepositoriesController(
    private val repositoryDataManager: RepositoryDataManager,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, GitManager>,
    private val notificationService: NotificationService
) {

    /**
     * Get all repositories
     */
    @GetMapping
    fun getAllRepositories() = repositoryDataManager.findAll().map(::OutputRepositoryDto).sortedBy { it.id }

    /**
     * Create a repo
     */
    @PostMapping
    fun createRepo(@RequestBody dto: InputRepositoryDto): OutputRepositoryDto? {
        val repository = payloadProcessors.getValue(dto.git).createRepo(dto)
        return if (repository != null) {
            notificationService.notify("Created repo ${repository.name} with id ${repository.id}")
            OutputRepositoryDto(repository)
        } else {
            notificationService.notify("Repository with name ${dto.name} is not found in ${dto.git.name.toLowerCase()}")
            null
        }
    }
}
