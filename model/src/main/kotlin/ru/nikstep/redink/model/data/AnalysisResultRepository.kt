package ru.nikstep.redink.model.data

import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.AnalysisPairLines
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisRepository
import java.time.LocalDateTime

open class AnalysisResultRepository(
    private val analysisRepository: AnalysisRepository,
    private val analysisPairRepository: AnalysisPairRepository,
    private val analysisPairLinesRepository: AnalysisPairLinesRepository
) {

    /**
     * Save all analysis results
     */
    @Transactional
    open fun saveAll(repository: Repository, analysisResults: Collection<AnalysisResult>) {
        val analysis = analysisRepository.save(
            Analysis(
                repository = repository,
                executionDate = LocalDateTime.now()
            )
        )
        for (analysisResult in analysisResults) {
            save(analysis, analysisResult)
        }
    }

    /**
     * Save an analysis result
     */
    @Transactional
    open fun save(analysis: Analysis, analysisResult: AnalysisResult) {
        val analysisPair = saveAnalysisResult(analysis, analysisResult)
        saveMatchedLines(analysisResult, analysisPair)
    }

    private fun saveMatchedLines(
        analysisResult: AnalysisResult,
        analysisPair: AnalysisPair
    ) {
        analysisPairLinesRepository.saveAll(analysisResult.matchedLines.map {
            AnalysisPairLines(
                from1 = it.match1.first,
                to1 = it.match1.second,
                from2 = it.match2.first,
                to2 = it.match2.second,
                fileName1 = it.files.first,
                fileName2 = it.files.second,
                analysisPair = analysisPair
            )
        })
    }

    private fun saveAnalysisResult(analysis: Analysis, analysisResult: AnalysisResult): AnalysisPair {
        return analysisPairRepository.save(analysisResult.run {
            AnalysisPair(
                student1 = students.first,
                student2 = students.second,
                lines = lines,
                repo = repo,
                percentage = percentage,
                student1Sha = sha.first,
                student2Sha = sha.second,
                gitService = gitService,
                analysis = analysis
            )
        })
    }
}