package io.gitplag.analysis.analyzer

import io.gitplag.analysis.analysisFilesDirectoryName
import io.gitplag.analysis.analyzer.Analyzer.Companion.repoInfo
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.*
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.util.asPath
import io.gitplag.util.generateDir
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.io.File
import java.time.LocalDateTime
import kotlin.math.roundToInt

/**
 * JPlag client wrapper
 */
class JPlagAnalyzer(
    private val sourceCodeStorage: SourceCodeStorage,
    private val analysisResultFilesDir: String,
    private val jplagResultDir: String,
    private val serverUrl: String
) :
    Analyzer {

    private val logger = KotlinLogging.logger {}
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()

    override fun analyze(settings: AnalysisSettings): AnalysisResult {
        val executionDate = LocalDateTime.now()
        val directoryName = analysisFilesDirectoryName(settings, executionDate)

        val fileDir = generateDir(analysisResultFilesDir, directoryName)
        val jplagReportDir = generateDir(jplagResultDir, directoryName)

        logger.info { "Analysis:JPlag:1.Gathering files for analysis. ${repoInfo(settings)}" }
        val analysisFiles = sourceCodeStorage.loadBasesAndComposedSolutions(settings, fileDir)

        logger.info { "Analysis:JPlag:2.Start analysis. ${repoInfo(settings)}" }
        JPlagClient(analysisFiles, jplagReportDir).run()

        val matchLines =
            if (settings.mode.order > AnalysisMode.LINK.order) {
                logger.info { "Analysis:JPlag:3.Start parsing of results. ${repoInfo(settings)}" }
                analysisFiles.toSolutionPairIndexes().mapNotNull { index ->
                    parseResults(index, settings, analysisFiles.solutions, jplagReportDir)
                }
            } else {
                logger.info { "Analysis:JPlag:3.Skipped parsing. ${repoInfo(settings)}" }
                emptyList()
            }

        val resultLink = "$serverUrl/jplagresult/$directoryName/index.html"

        logger.info { "Analysis:JPlag:4.End of analysis. ${repoInfo(settings)}" }
        return AnalysisResult(
            settings,
            resultLink,
            executionDate,
            matchLines
        )
    }

    private fun parseResults(
        index: Int,
        analysisSettings: AnalysisSettings,
        solutions: List<Solution>,
        resultDir: String
    ): AnalysisMatch? {
        val resultFile = File(asPath(resultDir, "match$index-link.html"))
        if (!resultFile.exists()) return null
        val body = Jsoup.parse(resultFile.readText())
            .body()
        val (name1, name2) = requireNotNull(regexUserNames.find(body.getElementsByTag("H3")[0].text()))
            .groupValues.subList(1, 3)
        val percentage = body.getElementsByTag("H1")[0].text().replace("%", "").toDouble().roundToInt()

        val matchedLines =
            if (analysisSettings.mode == AnalysisMode.FULL) {
                parseMatchedLines(index, resultDir)
            } else emptyList<MatchedLines>()

        val solution1 = findSolutionByStudent(solutions, name1)
        val solution2 = findSolutionByStudent(solutions, name2)
        return AnalysisMatch(
            students = name1 to name2,
            lines = -1,
            percentage = percentage,
            matchedLines = matchedLines,
            sha = solution1.sha to solution2.sha,
            createdAt = solution1.createdAt to solution2.createdAt
        )
    }

    private fun parseMatchedLines(
        index: Int,
        resultDir: String
    ): MutableList<MatchedLines> {
        val matchedLines = mutableListOf<MatchedLines>()
        val body2 = Jsoup.parse(File(asPath(resultDir, "match$index-top.html")).readText())
            .body()
        val rows = body2.getElementsByTag("tr")
        for (rowNumber in 1 until rows.size - 1) {
            val columns = rows[rowNumber].getElementsByTag("td")
            val (fileName1, from1, to1) = requireNotNull(regexMatchedRows.find(columns[1].text()))
                .groupValues.subList(1, 4)
            val (fileName2, from2, to2) = requireNotNull(regexMatchedRows.find(columns[2].text()))
                .groupValues.subList(1, 4)
            matchedLines += MatchedLines(
                match1 = from1.toInt() to to1.toInt(),
                match2 = from2.toInt() to to2.toInt(),
                files = fileName1 to fileName2
            )
        }
        return matchedLines
    }

    private fun PreparedAnalysisData.toSolutionPairIndexes(): IntRange {
        val countOfMatches = (0 until solutions.size).sum()
        return 0 until countOfMatches
    }
}