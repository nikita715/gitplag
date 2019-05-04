package io.gitplag.core.rest

import io.gitplag.model.manager.RepositoryDataManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RepositoryController(private val repositoryDataManager: RepositoryDataManager) {

    @GetMapping("/repo/{id}")
    fun getRepo(@PathVariable id: Long) = repositoryDataManager.findById(id)

}