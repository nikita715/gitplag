package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.analyser.Analyser.Companion.repoInfo
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.data.*
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.util.RandomGenerator
import ru.nikstep.redink.util.generateDir
import java.time.LocalDateTime

/**
 * Moss client wrapper
 */
class MossAnalyser(
    private val sourceCodeStorage: SourceCodeStorage,
    private val randomGenerator: RandomGenerator,
    private val analysisResultFilesDir: String,
    private val mossPath: String
) : Analyser {
    private val logger = KotlinLogging.logger {}

    private val extensionRegex = "\\.[a-zA-Z]+$".toRegex()

    override fun analyse(settings: AnalysisSettings): AnalysisResult {
        val (hashFileDir, fileDir) = generateDir(randomGenerator, analysisResultFilesDir)
        logger.info { "Analysis:Moss:1.Gathering files for analysis. ${repoInfo(settings)}" }
        val analysisFiles = sourceCodeStorage.loadBasesAndComposedSolutions(settings, fileDir)

        logger.info { "Analysis:Moss:2.Start analysis. ${repoInfo(settings)}" }
        val resultLink = MossClient(analysisFiles, mossPath).run()

        val matchData =
            if (settings.mode.order > AnalysisMode.LINK.order) {
                logger.info { "Analysis:Moss:3.Start parsing of results. ${repoInfo(settings)}" }
                parseResult(settings, analysisFiles.solutions, resultLink)
            } else {
                logger.info { "Analysis:JPlag:3.Skipped parsing. ${repoInfo(settings)}" }
                emptyList()
            }

        logger.info { "Analysis:Moss:4.End of analysis. ${repoInfo(settings)}" }
        val executionDate = LocalDateTime.now()
        return AnalysisResult(settings, resultLink, executionDate, matchData, hashFileDir)
    }

    private fun parseResult(
        analysisSettings: AnalysisSettings,
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
                val firstATag = tds[0].selectFirst("a")
                val secondATag = tds[1].selectFirst("a")

                val firstPath = firstATag.text().split(" ").first().split("/")
                val secondPath = secondATag.text().split(" ").first().split("/")

                val students =
                    firstPath.zip(secondPath)
                        .dropWhile { it.first == it.second }
                        .first().let {
                            it.first.replace(extensionRegex, "") to it.second.replace(extensionRegex, "")
                        }

                val lines = tds[2].text().toInt()

                val percentage = firstATag.text().split(" ")
                    .last()
                    .removeSurrounding("(", "%)")
                    .toInt()

                val solution1 = findSolutionByStudent(solutions, students.first)
                val solution2 = findSolutionByStudent(solutions, students.second)

                val matchedLines =
                    if (analysisSettings.mode == AnalysisMode.FULL) {
                        val rows = Jsoup.connect(firstATag.attr("href").replace(".html", "-top.html"))
                            .get().getElementsByTag("tr")
                        val matchedLines = mutableListOf<MatchedLines>()
                        for (row in rows.subList(1, rows.size)) {
                            val cells = row.getElementsByTag("td")
                            val firstMatch = cells[0].selectFirst("a").text().split("-")
                            val secondMatch = cells[2].selectFirst("a").text().split("-")
                            matchedLines += MatchedLines(
                                match1 = firstMatch[0].toInt() to firstMatch[1].toInt(),
                                match2 = secondMatch[0].toInt() to secondMatch[1].toInt(),
                                files = solution1.fileName to solution2.fileName
                            )
                        }
                        matchedLines
                    } else mutableListOf()

                AnalysisMatch(
                    students = students.first to students.second,
                    lines = lines,
                    percentage = percentage,
                    matchedLines = matchedLines,
                    sha = solution1.sha to solution2.sha,
                    createdAt = solution1.createdAt to solution2.createdAt
                )
            }
    }

}