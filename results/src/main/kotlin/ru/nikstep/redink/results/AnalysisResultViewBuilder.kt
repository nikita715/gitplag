package ru.nikstep.redink.results

import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.pre
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.repo.AnalysisPairRepository

/**
 * Builder of plagiarism analysis result pages
 */
class AnalysisResultViewBuilder(
    private val analysisPairRepository: AnalysisPairRepository,
    private val solutionStorage: SolutionStorage
) {

    /**
     * Build a two-column solution comparison page
     */
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