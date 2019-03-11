package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.User

/**
 * Spring data repo of [UserRepository]
 */
interface UserRepository : JpaRepository<User, Long> {

    /**
     * Find [User] by [name]
     */
    fun findByName(name: String): User

}