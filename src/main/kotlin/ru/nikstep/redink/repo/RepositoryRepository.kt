package ru.nikstep.redink.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.entity.Repository
import ru.nikstep.redink.entity.User

interface RepositoryRepository : JpaRepository<Repository, Long> {
    fun findByName(name: String): Repository
    fun deleteAllByOwner(owner: User)
}
