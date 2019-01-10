package ru.nikstep.redink.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.entity.User

interface UserRepository : JpaRepository<User, Long> {

    fun findByName(name: String): User

}