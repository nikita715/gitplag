package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.model.entity.Repository

/**
 * Moss client wrapper
 */
class MossAnalyser(
    private val solutionStorage: SolutionStorage,
    private val mossId: String
) : Analyser {
    private val logger = KotlinLogging.logger {}

    override fun analyse(repository: Repository): Collection<AnalysisResult> {
        val analysisFiles = solutionStorage.loadAllBasesAndSolutions(repository)
        return parseResult(
            repository,
            analysisFiles,
            resultLink = MossClient(analysisFiles, mossId).run()
        )
    }

    private fun parseResult(
        repository: Repository,
        analysisFiles: PreparedAnalysisFiles,
        resultLink: String
    ): Collection<AnalysisResult> =
        Jsoup.connect(resultLink).get()
            .body()
            .getElementsByTag("table")
            .select("tr")
            .drop(1)
            .map { tr -> tr.select("td") }
            .mapNotNull { tds ->
                val first = tds[0].selectFirst("a")
                val second = tds[1].selectFirst("a")

                val firstPath = first.text().split(" ").first().split("/")
                val secondPath = second.text().split(" ").first().split("/")

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
                val matchedLines = mutableListOf<MatchedLines>()
                for (row in rows.subList(1, rows.size)) {
                    val cells = row.getElementsByTag("td")
                    val firstMatch = cells[0].selectFirst("a").text().split("-")
                    val secondMatch = cells[2].selectFirst("a").text().split("-")
                    matchedLines += MatchedLines(
                        match1 = firstMatch[0].toInt() to firstMatch[1].toInt(),
                        match2 = secondMatch[0].toInt() to secondMatch[1].toInt(),
                        files = analysisFiles.solutions.getValue(students.first).fileName
                                to analysisFiles.solutions.getValue(students.second).fileName
                    )
                }
                AnalysisResult(
                    students = students.first to students.second,
                    sha = analysisFiles.solutions.getValue(students.first).sha to
                            analysisFiles.solutions.getValue(students.second).sha,
                    lines = lines,
                    percentage = percentage,
                    repo = repository.name,
                    matchedLines = matchedLines,
                    gitService = repository.gitService
                )
            }

    private fun Pair<String, String>.sort() {
        first.compareTo(second)
    }
}