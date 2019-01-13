package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.User

interface RepositoryRepository : JpaRepository<Repository, Long> {
    fun findByName(name: String): Repository
    fun deleteAllByOwner(owner: User)
}
