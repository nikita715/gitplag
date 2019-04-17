package io.gitplag.model.repo

import io.gitplag.model.entity.AnalysisPairLines
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [AnalysisPairLines]
 */
interface AnalysisPairLinesRepository : JpaRepository<AnalysisPairLines, Long>