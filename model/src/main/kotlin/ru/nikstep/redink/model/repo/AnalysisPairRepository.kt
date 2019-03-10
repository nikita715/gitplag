package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.nikstep.redink.model.entity.AnalysisPair

/**
 * Spring data repo of [AnalysisPair]
 */
interface AnalysisPairRepository : JpaRepository<AnalysisPair, Long> {

    /**
     * Delete [AnalysisPair]
     */
    fun deleteByStudent1AndStudent2AndRepo(
        student1: String,
        student2: String,
        repo: String
    )

    /**
     * Find all analysis pairs of the [repo]
     */
    fun findAllByRepoOrderByIdDesc(repo: String): List<AnalysisPair>

    /**
     * Find an analysis pair
     */
    fun findByRepoAndStudent1AndStudent2OrderByIdDesc(
        repo: String,
        student1: String,
        student2: String
    ): AnalysisPair?

    /**
     * Find all analysis pairs of the [repo] and a [student]
     */
    @Query("from AnalysisPair ap where ap.repo = ?1 and (ap.student1 = ?2 OR ap.student2 = ?2) order by ap.id desc ")
    fun findAllByRepoAndStudentOrderByIdDesc(repo: String, student: String): List<AnalysisPair>

    /**
     * Find all analysis pairs of the [repo] and two students
     */
    fun findAllByRepoAndStudent1AndStudent2OrderByIdDesc(
        repo: String,
        student1: String,
        student2: String
    ): List<AnalysisPair>
}