package io.gitplag.analysis.analyzer

import io.gitplag.analysis.analysisFilesDirectoryName
import io.gitplag.analysis.repoInfo
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.*
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.util.generateDir
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.time.LocalDateTime

/**
 * Moss client wrapper
 */
class MossAnalyzer(
    private val sourceCodeStorage: SourceCodeStorage,
    private val analysisResultFilesDir: String,
    private val mossId: String
) : Analyzer {
    private val logger = KotlinLogging.logger {}

    private val extensionRegex = "\\.[a-zA-Z]+$".toRegex()

    override fun analyze(settings: AnalysisSettings): AnalysisResult {
        val executionDate = LocalDateTime.now()
        val directoryName = analysisFilesDirectoryName(settings, executionDate)
        val fileDir = generateDir(analysisResultFilesDir, directoryName)
        logger.info { "Analysis:Moss:1.Gathering files for analysis. ${repoInfo(settings)}" }
        val analysisFiles = sourceCodeStorage.loadBasesAndComposedSolutions(settings, fileDir)

        logger.info { "Analysis:Moss:2.Start analysis. ${repoInfo(settings)}" }
        val resultLink = MossClient(analysisFiles, mossId).run()

        logger.info { "Analysis:Moss:2.Moss result. $resultLink" }
        val matchData =
            if (settings.analysisMode.order > AnalysisMode.LINK.order) {
                logger.info { "Analysis:Moss:3.Start parsing of results. ${repoInfo(settings)}" }
                parseResult(settings, analysisFiles.solutions, resultLink)
            } else {
                logger.info { "Analysis:JPlag:3.Skipped parsing. ${repoInfo(settings)}" }
                emptyList()
            }

        logger.info { "Analysis:Moss:4.End of analysis. ${repoInfo(settings)}" }
        return AnalysisResult(settings, resultLink, executionDate, matchData)
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

                val students = firstATag.text().split(" ").first() to secondATag.text().split(" ").first()

//                val lines = tds[2].text().toInt()

                val percentage1 = firstATag.text().split(" ")
                    .last()
                    .removeSurrounding("(", "%)")
                    .toInt()
                val percentage2 = secondATag.text().split(" ")
                    .last()
                    .removeSurrounding("(", "%)")
                    .toInt()

                val percentage = if (percentage1 > percentage2) percentage1 else percentage2

                val solution1 = findSolutionByStudent(solutions, students.first)
                val solution2 = findSolutionByStudent(solutions, students.second)

                val notReversed = students.first < students.second

                val matchedLines =
                    if (analysisSettings.analysisMode == AnalysisMode.FULL) {
                        val rows = Jsoup.connect(firstATag.attr("href").replace(".html", "-top.html"))
                            .get().getElementsByTag("tr")
                        val matchedLines = mutableListOf<MatchedLines>()
                        for (row in rows.subList(1, rows.size)) {
                            val cells = row.getElementsByTag("td")
                            val firstMatch = cells[0].selectFirst("a").text().split("-")
                            val secondMatch = cells[2].selectFirst("a").text().split("-")
                            matchedLines += if (notReversed)
                                MatchedLines(
                                    match1 = firstMatch[0].toInt() to firstMatch[1].toInt(),
                                    match2 = secondMatch[0].toInt() to secondMatch[1].toInt(),
                                    files = solution1.fileName to solution2.fileName
                                )
                            else
                                MatchedLines(
                                    match2 = firstMatch[0].toInt() to firstMatch[1].toInt(),
                                    match1 = secondMatch[0].toInt() to secondMatch[1].toInt(),
                                    files = solution2.fileName to solution1.fileName
                                )
                        }
                        matchedLines
                    } else mutableListOf()

                if (notReversed)
                    AnalysisMatch(
                        students = students.first to students.second,
                        percentage = percentage,
                        minPercentage = percentage,
                        maxPercentage = percentage,
                        matchedLines = matchedLines,
                        sha = solution1.sha to solution2.sha,
                        createdAt = solution1.createdAt to solution2.createdAt
                    )
                else
                    AnalysisMatch(
                        students = students.second to students.first,
                        percentage = percentage,
                        minPercentage = percentage,
                        maxPercentage = percentage,
                        matchedLines = matchedLines,
                        sha = solution2.sha to solution1.sha,
                        createdAt = solution2.createdAt to solution1.createdAt
                    )
            }
    }

}