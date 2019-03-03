package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.Repository

/**
 * Spring data repo of [Repository]
 */
interface RepositoryRepository : JpaRepository<Repository, Long> {

    /**
     * Find a [Repository] by [name]
     */
    fun findByName(name: String): Repository
}
