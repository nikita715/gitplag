package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.entity.User

interface SourceCodeRepository : JpaRepository<SourceCode, Long> {
    fun findByUserAndRepoAndFileName(user: User, repo: Repository, fileName: String): SourceCode?
}