package ru.nikstep.redink.model.data

import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.AnalysisPairLines
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository

class AnalysisResultRepository(
    private val analysisPairRepository: AnalysisPairRepository,
    private val analysisPairLinesRepository: AnalysisPairLinesRepository
) {

    fun save(analysisResults: Set<AnalysisResult>) {
        analysisResults.forEach { save(it) }
    }

    fun save(analysisResult: AnalysisResult) {
        analysisPairRepository.deleteByStudent1AndStudent2AndRepoAndFileName(
            student1 = analysisResult.students.first,
            student2 = analysisResult.students.second,
            repo = analysisResult.repository,
            fileName = analysisResult.fileName
        )
        val analysisPair = analysisPairRepository.save(
            AnalysisPair(
                student1 = analysisResult.students.first,
                student2 = analysisResult.students.second,
                fileName = analysisResult.fileName,
                lines = analysisResult.countOfLines,
                repo = analysisResult.repository,
                percentage = analysisResult.percentage
            )
        )
        analysisPairLinesRepository.saveAll(analysisResult.matchedLines.map {
            val analysisPairLines = AnalysisPairLines(
                from1 = it.first.first,
                to1 = it.first.second,
                from2 = it.second.first,
                to2 = it.second.second,
                analysisPair = analysisPair
            )
            analysisPairLines
        })
    }

}