package io.gitplag.model.manager

import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.AnalysisPair
import io.gitplag.model.entity.AnalysisPairLines
import io.gitplag.model.repo.AnalysisPairLinesRepository
import io.gitplag.model.repo.AnalysisPairRepository
import io.gitplag.model.repo.AnalysisRepository
import io.gitplag.model.util.analysisResultComparator
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
                resultLink = analysisResults.resultLink,
                studentsWithoutSolutions = analysisResults.studentsWithoutSolutions
                    .sorted().joinToString(separator = ", ")
            )
        )
        val analysisPairs = analysisResults.matchData.sortedWith(analysisResultComparator).map { pair ->
            if (pair.createdAt.first.isBefore(pair.createdAt.second)) {
                val analysisPair = analysisPairRepository.save(
                    AnalysisPair(
                        student1 = pair.students.first,
                        student2 = pair.students.second,
                        percentage = pair.percentage,
                        minPercentage = pair.minPercentage,
                        maxPercentage = pair.maxPercentage,
                        analysis = analysis,
                        sha1 = pair.sha.first,
                        sha2 = pair.sha.second,
                        createdAt1 = pair.createdAt.first,
                        createdAt2 = pair.createdAt.second
                    )
                )
                val analysisPairLines = analysisPairLinesRepository.saveAll(pair.matchedLines.map {
                    AnalysisPairLines(
                        from1 = it.match1.first,
                        to1 = it.match1.second,
                        from2 = it.match2.first,
                        to2 = it.match2.second,
                        fileName1 = it.files.first,
                        fileName2 = it.files.second,
                        analysisPair = analysisPair,
                        analyzer = it.analyzer
                    )
                })
                analysisPair to analysisPairLines
            } else {
                val analysisPair = analysisPairRepository.save(
                    AnalysisPair(
                        student1 = pair.students.second,
                        student2 = pair.students.first,
                        percentage = pair.percentage,
                        minPercentage = pair.minPercentage,
                        maxPercentage = pair.maxPercentage,
                        analysis = analysis,
                        sha1 = pair.sha.second,
                        sha2 = pair.sha.first,
                        createdAt1 = pair.createdAt.second,
                        createdAt2 = pair.createdAt.first
                    )
                )
                val analysisPairLines = analysisPairLinesRepository.saveAll(pair.matchedLines.map { lines ->
                    AnalysisPairLines(
                        from1 = lines.match2.first,
                        to1 = lines.match2.second,
                        from2 = lines.match1.first,
                        to2 = lines.match1.second,
                        fileName1 = lines.files.second,
                        fileName2 = lines.files.first,
                        analysisPair = analysisPair,
                        analyzer = lines.analyzer
                    )
                })
                analysisPair to analysisPairLines
            }
        }
        val res = analysisPairs.map { analysisPairRepository.save(it.first.copy(analysisPairLines = it.second)) }
        return analysisRepository.save(analysis.copy(analysisPairs = res))
    }
}