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
    fun deleteByStudent1AndStudent2AndRepoAndFileName(
        student1: String,
        student2: String,
        repo: String,
        fileName: String
    )

    /**
     * Find all analysis pairs of the [repo]
     */
    fun findAllByRepo(repo: String): List<AnalysisPair>

    /**
     * Find all analysis pairs of the [repo] and [fileName]
     */
    fun findAllByRepoAndFileName(repo: String, fileName: String): List<AnalysisPair>

    /**
     * Find all analysis pairs of the [repo], [fileName] and [student]
     */
    @Query("from AnalysisPair ap where ap.repo = ?1 and ap.fileName = ?2 and (ap.student1 = ?3 OR ap.student2 = ?3)")
    fun findAllByRepoAndFileNameAndStudent(
        repo: String,
        fileName: String,
        student: String
    ): List<AnalysisPair>

    /**
     * Find an analysis pair
     */
    fun findByRepoAndFileNameAndStudent1AndStudent2(
        repo: String,
        fileName: String,
        student1: String,
        student2: String
    ): AnalysisPair?

    /**
     * Find all analysis pairs of the [repo] and a [student]
     */
    @Query("from AnalysisPair ap where ap.repo = ?1 and (ap.student1 = ?2 OR ap.student2 = ?2)")
    fun findAllByRepoAndStudent(repo: String, student: String): List<AnalysisPair>

    /**
     * Find all analysis pairs of the [repo] and two students
     */
    fun findAllByRepoAndStudent1AndStudent2(repo: String, student1: String, student2: String): List<AnalysisPair>
}