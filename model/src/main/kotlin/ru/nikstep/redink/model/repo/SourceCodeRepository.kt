package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.SourceCode

interface SourceCodeRepository : JpaRepository<SourceCode, Long> {
    fun findByUserIdAndRepoIdAndFileName(userId: Long, repoId: Long, fileName: String): SourceCode?
    fun findAllByRepoIdAndFileName(repoId: Long, fileName: String): List<SourceCode>
}