package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.User

interface UserRepository : JpaRepository<User, Long> {

    fun findByName(name: String): User
    fun findByInstallationId(installationId: Long): User

}