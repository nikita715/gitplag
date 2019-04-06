package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.AnalysisPair

/**
 * Spring data repo of [AnalysisPair]
 */
interface AnalysisPairRepository : JpaRepository<AnalysisPair, Long> {

    fun findByAnalysisIdAndStudent1AndStudent2(analysisId: Long, student1: String, student2: String): AnalysisPair?

}