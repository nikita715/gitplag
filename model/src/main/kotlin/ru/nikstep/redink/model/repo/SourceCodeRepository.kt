package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.SourceCode

/**
 * Spring data repo of [SourceCode]
 */
interface SourceCodeRepository : JpaRepository<SourceCode, Long> {

    /**
     * Find all solutions for [repo] and [fileName]
     */
    fun findAllByRepoAndFileName(repo: String, fileName: String): List<SourceCode>
}