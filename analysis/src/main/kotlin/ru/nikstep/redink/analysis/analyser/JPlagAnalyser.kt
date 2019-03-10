package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.AnalysisSettings
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import ru.nikstep.redink.util.inTempDirectory
import java.io.File
import kotlin.math.roundToInt

/**
 * JPlag client wrapper
 */
class JPlagAnalyser(private val solutionStorage: SolutionStorage, private val solutionsDir: String) :
    Analyser {

    private val logger = KotlinLogging.logger {}
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()

    override fun analyse(analysisSettings: AnalysisSettings): Collection<AnalysisResult> =
        inTempDirectory { resultDir ->
            val analysisFiles = solutionStorage.loadAllBasesAndSolutions(analysisSettings)
            val solutionsPath = solutionsDir.asPathInRoot() + "/" + analysisSettings.gitService.toString()
            JPlagClient(analysisFiles, solutionsPath, resultDir).run()
            analysisFiles.indexRangeOfEachToEachStudentPair().map { index ->
                parseResults(analysisSettings, analysisFiles, resultDir, index)
            }
        }

    private fun parseResults(
        analysisSettings: AnalysisSettings,
        analysisFiles: PreparedAnalysisFiles,
        resultDir: String,
        index: Int
    ): AnalysisResult {
        val body = Jsoup.parse(File(asPath(resultDir, "match$index-link.html")).readText())
            .body()
        val (name1, name2) = regexUserNames.find(body.getElementsByTag("H3")[0].text())!!
            .groupValues.subList(1, 3)
        val percentage = body.getElementsByTag("H1")[0].text().replace("%", "").toDouble().roundToInt()
        val body2 = Jsoup.parse(File(asPath(resultDir, "match$index-top.html")).readText())
            .body()
        val rows = body2.getElementsByTag("tr")
        val matchedLines = mutableListOf<MatchedLines>()
        for (rowNumber in 1 until rows.size - 1) {
            val columns = rows[rowNumber].getElementsByTag("td")
            val (fileName1, from1, to1) = regexMatchedRows.find(columns[1].text())!!
                .groupValues.subList(1, 4)
            val (fileName2, from2, to2) = regexMatchedRows.find(columns[2].text())!!
                .groupValues.subList(1, 4)
            matchedLines += MatchedLines(
                match1 = from1.toInt() to to1.toInt(),
                match2 = from2.toInt() to to2.toInt(),
                files = fileName1 to fileName2
            )
        }
        return AnalysisResult(
            students = name1 to name2,
            sha = analysisFiles.solutions.getValue(name1).sha to analysisFiles.solutions.getValue(name2).sha,
            lines = -1,
            percentage = percentage,
            repo = analysisFiles.repoName,
            gitService = analysisSettings.gitService,
            matchedLines = matchedLines
        )
    }

    private fun PreparedAnalysisFiles.indexRangeOfEachToEachStudentPair(): IntRange {
        val countOfMatches = (0 until solutions.size).sum()
        return 0 until countOfMatches
    }
}