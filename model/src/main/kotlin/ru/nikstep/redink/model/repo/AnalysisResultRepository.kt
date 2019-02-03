package ru.nikstep.redink.model.repo

import ru.nikstep.redink.data.AnalysisResult
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.AnalysisPairLines

class AnalysisResultRepository(
    private val analysisPairRepository: AnalysisPairRepository,
    private val analysisPairLinesRepository: AnalysisPairLinesRepository
) {

    fun save(analysisResults: Set<AnalysisResult>) {
        analysisResults.forEach { save(it) }
    }

    fun save(analysisResult: AnalysisResult) {
        val analysisPair = analysisPairRepository.save(
            AnalysisPair(
                student1 = analysisResult.students.first,
                student2 = analysisResult.students.second,
                lines = analysisResult.countOfLines,
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