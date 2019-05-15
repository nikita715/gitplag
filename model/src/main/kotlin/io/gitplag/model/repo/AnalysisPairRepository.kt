package io.gitplag.model.repo

import io.gitplag.model.entity.AnalysisPair
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [AnalysisPair]
 */
interface AnalysisPairRepository : JpaRepository<AnalysisPair, Long>