package ru.nikstep.redink.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.entity.Repository
import ru.nikstep.redink.entity.SourceCode
import ru.nikstep.redink.entity.User

interface SourceCodeRepository : JpaRepository<SourceCode, Long> {
    fun findByUserAndRepoAndFileName(user: User, repo: Repository, fileName: String): SourceCode?
}