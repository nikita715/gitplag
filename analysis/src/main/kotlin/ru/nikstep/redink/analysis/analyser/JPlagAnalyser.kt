package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.analyser.Analyser.Companion.repoInfo
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.data.*
import ru.nikstep.redink.model.entity.JPlagReport
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.repo.JPlagReportRepository
import ru.nikstep.redink.util.RandomGenerator
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.generateDir
import java.io.File
import java.time.LocalDateTime
import kotlin.math.roundToInt

/**
 * JPlag client wrapper
 */
class JPlagAnalyser(
    private val sourceCodeStorage: SourceCodeStorage,
    private val randomGenerator: RandomGenerator,
    private val jPlagReportRepository: JPlagReportRepository,
    private val analysisResultFilesDir: String,
    private val jplagResultDir: String
) :
    Analyser {

    private val logger = KotlinLogging.logger {}
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()

    override fun analyse(settings: AnalysisSettings): AnalysisResult {
        val (hashFileDir, fileDir) = generateDir(randomGenerator, analysisResultFilesDir)
        val (hashJplagReport, jplagReportDir) = generateDir(randomGenerator, jplagResultDir)

        logger.info { "Analysis:JPlag:1.Gathering files for analysis. ${repoInfo(settings)}" }
        val analysisFiles = sourceCodeStorage.loadBasesAndSeparatedSolutions(settings, fileDir)

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

        val resultLink = "/jplagresult/$hashJplagReport/index.html"
        val executionDate = LocalDateTime.now()
        jPlagReportRepository.save(JPlagReport(createdAt = executionDate, hash = hashJplagReport))

        logger.info { "Analysis:JPlag:4.End of analysis. ${repoInfo(settings)}" }
        return AnalysisResult(settings, resultLink, executionDate, matchLines, hashFileDir)
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