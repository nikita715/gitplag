package io.gitplag.model.manager

import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.AnalysisPair
import io.gitplag.model.entity.AnalysisPairLines
import io.gitplag.model.repo.AnalysisPairLinesRepository
import io.gitplag.model.repo.AnalysisPairRepository
import io.gitplag.model.repo.AnalysisRepository
import org.springframework.transaction.annotation.Transactional

/**
 * Data manager of [AnalysisResult]
 */
@Transactional
class AnalysisResultDataManager(
    private val analysisRepository: AnalysisRepository,
    private val analysisPairRepository: AnalysisPairRepository,
    private val analysisPairLinesRepository: AnalysisPairLinesRepository
) {

    /**
     * Save all analysis results
     */
    @Transactional
    fun saveAnalysis(analysisSettings: AnalysisSettings, analysisResults: AnalysisResult): Analysis {
        val analysis = analysisRepository.save(
            Analysis(
                repository = analysisSettings.repository,
                executionDate = analysisResults.executionDate,
                language = analysisSettings.language,
                analyzer = analysisSettings.analyzer,
                branch = analysisSettings.branch,
                resultLink = analysisResults.resultLink
            )
        )
        val analysisPairs = analysisResults.matchData.map {
            val analysisPair = analysisPairRepository.save(
                AnalysisPair(
                    student1 = it.students.first,
                    student2 = it.students.second,
                    lines = it.lines,
                    percentage = it.percentage,
                    analysis = analysis,
                    sha1 = it.sha.first,
                    sha2 = it.sha.second,
                    createdAt1 = it.createdAt.first,
                    createdAt2 = it.createdAt.second
                )
            )
            val analysisPairLines = analysisPairLinesRepository.saveAll(it.matchedLines.map {
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
            analysisPair to analysisPairLines
        }
        val res = analysisPairs.map { analysisPairRepository.save(it.first.copy(analysisPairLines = it.second)) }
        return analysisRepository.save(analysis.copy(analysisPairs = res))
    }
}