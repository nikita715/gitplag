package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.model.data.findSolutionByStudent
import ru.nikstep.redink.model.entity.JPlagReport
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.repo.JPlagReportRepository
import ru.nikstep.redink.util.RandomGenerator
import ru.nikstep.redink.util.asPath
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import kotlin.math.roundToInt

/**
 * JPlag client wrapper
 */
class JPlagAnalyser(
    private val sourceCodeStorage: SourceCodeStorage,
    private val randomGenerator: RandomGenerator,
    private val jPlagReportRepository: JPlagReportRepository,
    private val solutionsDir: String,
    private val jplagResultDir: String,
    private val serverUrl: String
) :
    Analyser {

    private val logger = KotlinLogging.logger {}
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()

    override fun analyse(settings: AnalysisSettings): AnalysisResult {
        val (hash, resultDir) = generateResultDir()

        logger.info { "Analysis:JPlag:1.Gathering files for analysis. ${repoInfo(settings)}" }
        val analysisFiles = sourceCodeStorage.loadBasesAndSeparatedSolutions(settings)

        logger.info { "Analysis:JPlag:2.Start analysis. ${repoInfo(settings)}" }
        JPlagClient(analysisFiles, solutionsDir, settings.branch, resultDir).run()

        val matchLines =
            if (settings.mode.order > AnalysisMode.LINK.order) {
                logger.info { "Analysis:JPlag:3.Start parsing of results. ${repoInfo(settings)}" }
                analysisFiles.toSolutionPairIndexes().mapNotNull { index ->
                    parseResults(index, settings, analysisFiles.solutions, resultDir)
                }
            } else {
                logger.info { "Analysis:JPlag:3.Skipped parsing. ${repoInfo(settings)}" }
                emptyList()
            }

        val resultLink = "$serverUrl/jplagresult/$hash/index.html"
        val executionDate = LocalDateTime.now()
        jPlagReportRepository.save(JPlagReport(createdAt = executionDate, hash = hash))

        logger.info { "Analysis:JPlag:4.End of analysis. ${repoInfo(settings)}" }
        return AnalysisResult(settings, resultLink, executionDate, matchLines)
    }

    private fun repoInfo(analysisSettings: AnalysisSettings): String =
        analysisSettings.run { "Repo ${repository.name}, Branch $branch." }

    private fun generateResultDir(): Pair<String, String> {
        val hash = randomGenerator.randomAlphanumeric(10)
        val file = File(jplagResultDir + hash)
        Files.createDirectory(file.toPath())
        val resultDir = file.absolutePath
        return Pair(hash, resultDir)
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

        return AnalysisMatch(
            students = name1 to name2,
            lines = -1,
            percentage = percentage,
            matchedLines = matchedLines,
            sha = findSolutionByStudent(solutions, name1).sha
                    to findSolutionByStudent(solutions, name2).sha
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