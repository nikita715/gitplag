package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.model.data.findByStudent
import java.time.LocalDateTime

/**
 * Moss client wrapper
 */
class MossAnalyser(
    private val solutionStorage: SolutionStorage,
    private val mossId: String
) : Analyser {
    private val logger = KotlinLogging.logger {}

    override fun analyse(analysisSettings: AnalysisSettings): AnalysisResult {
        val analysisFiles = solutionStorage.loadBasesAndComposedSolutions(analysisSettings)
        val resultLink = MossClient(analysisFiles, mossId).run()
        val matchData = parseResult(analysisFiles.solutions, resultLink)
        val executionDate = LocalDateTime.now()
        return AnalysisResult(analysisSettings, resultLink, executionDate, matchData)
    }

    private fun parseResult(
        solutions: List<Solution>,
        resultLink: String
    ): List<AnalysisMatch> {
        return Jsoup.connect(resultLink).get()
            .body()
            .getElementsByTag("table")
            .select("tr")
            .drop(1)
            .map { tr -> tr.select("td") }
            .mapNotNull { tds ->
                val first = tds[0].selectFirst("a")
                val second = tds[1].selectFirst("a")

                val first1 = first.text().split(" ").first()
                val firstPath = first1.split("/")
                val first2 = second.text().split(" ").first()
                val secondPath = first2.split("/")

                val students =
                    firstPath.zip(secondPath)
                        .dropWhile { it.first == it.second }
                        .first()

                val lines = tds[2].text().toInt()

                val percentage = first.text().split(" ")
                    .last()
                    .removeSurrounding("(", "%)")
                    .toInt()

                val rows = Jsoup.connect(first.attr("href").replace(".html", "-top.html"))
                    .get().getElementsByTag("tr")
                val matchedLines1 = mutableListOf<Pair<Int, Int>>()
                val matchedLines2 = mutableListOf<Pair<Int, Int>>()
                for (row in rows.subList(1, rows.size)) {
                    val cells = row.getElementsByTag("td")
                    val firstMatch = cells[0].selectFirst("a").text().split("-")
                    val secondMatch = cells[2].selectFirst("a").text().split("-")
                    matchedLines1 += firstMatch[0].toInt() to firstMatch[1].toInt()
                    matchedLines2 += secondMatch[0].toInt() to secondMatch[1].toInt()
                }

                val solution1 = findByStudent(solutions, students.first)
                val solution2 = findByStudent(solutions, students.second)

                val fileNames1 = matchedLines1.map {
                    var index = 0
                    for (i in solution1.lengths) {
                        if (it.first >= i) {
                            index++
                        }
                    }
                    if (index > 0)
                        solution1.files[index] to (it.first - solution1.lengths[index - 1] to it.second - solution1.lengths[index - 1]) else
                        solution1.files[index] to it
                }
                val fileNames2 = matchedLines2.map {
                    var index = 0
                    for (i in solution2.lengths) {
                        if (it.first >= i) {
                            index++
                        }
                    }
                    if (index > 0)
                        solution2.files[index] to (it.first - solution2.lengths[index - 1] to it.second - solution2.lengths[index - 1]) else
                        solution2.files[index] to it
                }

                AnalysisMatch(
                    students = students.first to students.second,
                    lines = lines,
                    percentage = percentage,
                    matchedLines = (0 until fileNames1.size).map { i ->
                        MatchedLines(
                            match1 = fileNames1[i].second.first to fileNames1[i].second.second,
                            match2 = fileNames2[i].second.first to fileNames2[i].second.second,
                            files = fileNames1[i].first to fileNames2[i].first,
                            sha = "" to ""
                        )
                    }
                )
            }
    }
}