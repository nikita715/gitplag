package ru.nikstep.redink.results

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.repo.AnalysisPairRepository

class AnalysisResultViewBuilder(
    private val analysisPairRepository: AnalysisPairRepository,
    private val solutionStorage: SolutionStorage
) {
    fun build(id: Long): String {
        val analysisPair = analysisPairRepository.findById(id).get()
        return createHTML().html {
            body {
                val solution1 = solutionStorage.loadSolution1(analysisPair).readLines()
                val solution2 = solutionStorage.loadSolution2(analysisPair).readLines()
                table {
                    tr {
                        td {
                            +analysisPair.student1
                        }
                        td {
                            +analysisPair.student2
                        }
                    }
                    tr {
                        td {
                            for (line in solution1) {
                                pre {
                                    +line
                                }
                            }
                        }
                        td {
                            for (line in solution2) {
                                pre {
                                    +line
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}