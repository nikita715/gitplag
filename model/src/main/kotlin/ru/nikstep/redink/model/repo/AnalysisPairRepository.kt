package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.nikstep.redink.model.entity.AnalysisPair

interface AnalysisPairRepository : JpaRepository<AnalysisPair, Long> {
    fun deleteByStudent1AndStudent2AndRepoAndFileName(
        student1: String,
        student2: String,
        repo: String,
        fileName: String
    )

    fun findAllByRepo(repo: String): List<AnalysisPair>

    fun findAllByRepoAndFileName(repo: String, fileName: String): List<AnalysisPair>

    @Query("from AnalysisPair ap where ap.repo = ?1 and ap.fileName = ?2 and (ap.student1 = ?3 OR ap.student2 = ?3)")
    fun findAllByRepoAndFileNameAndStudent(
        repo: String,
        fileName: String,
        student: String
    ): List<AnalysisPair>

    fun findAllByRepoAndFileNameAndStudent1AndStudent2(
        repo: String,
        fileName: String,
        student1: String,
        student2: String
    ): AnalysisPair?

    @Query("from AnalysisPair ap where ap.repo = ?1 and (ap.student1 = ?2 OR ap.student2 = ?2)")
    fun findAllByRepoAndStudent(repo: String, student: String): List<AnalysisPair>

    fun findAllByRepoAndStudent1AndStudent2(repo: String, student1: String, student2: String): List<AnalysisPair>
}