package ru.nikstep.redink.model.data

import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.AnalysisPairLines
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository

/**
 * Repository for storing analysis pairs data
 */
open class AnalysisResultRepository(
    private val analysisPairRepository: AnalysisPairRepository,
    private val analysisPairLinesRepository: AnalysisPairLinesRepository
) {

    /**
     * Save all analysis results
     */
    @Synchronized
    fun saveAll(analysisResults: Collection<AnalysisResult>) {
        for (analysisResult in analysisResults) {
            save(analysisResult)
        }
    }

    /**
     * Save an analysis result
     */
    @Transactional
    @Synchronized
    open fun save(analysisResult: AnalysisResult) {
        deleteExistingAnalysisResult(analysisResult)
        val analysisPair = saveAnalysisResult(analysisResult)
        saveMatchedLines(analysisResult, analysisPair)
    }

    private fun saveMatchedLines(
        analysisResult: AnalysisResult,
        analysisPair: AnalysisPair
    ) {
        analysisPairLinesRepository.saveAll(analysisResult.matchedLines.map {
            AnalysisPairLines(
                from1 = it.first.first,
                to1 = it.first.second,
                from2 = it.second.first,
                to2 = it.second.second,
                analysisPair = analysisPair
            )
        })
    }

    private fun saveAnalysisResult(analysisResult: AnalysisResult): AnalysisPair {
        return analysisPairRepository.save(
            AnalysisPair(
                student1 = analysisResult.students.first,
                student2 = analysisResult.students.second,
                fileName = analysisResult.fileName,
                lines = analysisResult.countOfLines,
                repo = analysisResult.repository,
                percentage = analysisResult.percentage,
                student1Sha = analysisResult.sha.first,
                student2Sha = analysisResult.sha.second,
                gitService = analysisResult.gitService
            )
        )
    }

    private fun deleteExistingAnalysisResult(analysisResult: AnalysisResult) {
        analysisPairRepository.deleteByStudent1AndStudent2AndRepoAndFileName(
            student1 = analysisResult.students.first,
            student2 = analysisResult.students.second,
            repo = analysisResult.repository,
            fileName = analysisResult.fileName
        )
    }

}