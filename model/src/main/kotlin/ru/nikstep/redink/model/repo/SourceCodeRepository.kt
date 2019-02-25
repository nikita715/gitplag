package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.SourceCode

interface SourceCodeRepository : JpaRepository<SourceCode, Long> {
    fun findAllByRepoAndFileName(repo: String, fileName: String): List<SourceCode>
}