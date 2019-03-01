package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import ru.nikstep.redink.util.inTempDirectory
import ru.nikstep.redink.util.onlyLastName
import java.io.File
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.math.roundToInt

class JPlagAnalyser(solutionStorage: SolutionStorage, private val solutionsPath: String) :
    AbstractAnalyser(solutionStorage) {

    private val logger = KotlinLogging.logger {}
    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()

    override fun PreparedAnalysisFiles.processFiles(pullRequest: PullRequest): Iterable<AnalysisResult> =
        inTempDirectory { resultDir ->
            executeJPlag(this, resultDir)
            rangeOfMatchIndexes(this).map { index ->
                index.let(toAnalysisResults(this, resultDir))
            }.filter {
                it.students.first == pullRequest.creatorName || it.students.second == pullRequest.creatorName
            }
        }

    private fun executeJPlag(analysisFiles: PreparedAnalysisFiles, resultDir: String) =
        buildString {
            append("java -jar $jplagPath ")
            append("-l ${analysisFiles.language.ofJPlag()} ")
            append("-bc .base ")
            append("-r $resultDir ")
            append("-p ${analysisFiles.fileName.onlyLastName()} ")
            append("-s ")
            append(asPath(solutionsPath, analysisFiles.repoName))
        }.also {
            logger.info { "Analysis: start execution of $it" }
            Runtime.getRuntime().exec(it).waitFor(5, MINUTES)
        }


    private fun toAnalysisResults(
        analysisFiles: PreparedAnalysisFiles,
        resultDir: String
    ): (Int) -> AnalysisResult = { numberOfMatch: Int ->
        val body = Jsoup.parse(File(asPath(resultDir, "match$numberOfMatch-link.html")).readText())
            .body()
        val (name1, name2) = regexUserNames.find(body.getElementsByTag("H3")[0].text())!!
            .groupValues.subList(1, 3)
        val percentage = body.getElementsByTag("H1")[0].text().replace("%", "").toDouble().roundToInt()
        val body2 = Jsoup.parse(File(asPath(resultDir, "match$numberOfMatch-top.html")).readText())
            .body()
        val rows = body2.getElementsByTag("tr")
        val matchedLines = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (rowNumber in 1 until rows.size - 1) {
            val columns = rows[rowNumber].getElementsByTag("td")
            val (fileName1, from1, to1) = regexMatchedRows.find(columns[1].text())!!
                .groupValues.subList(1, 4)
            val (fileName2, from2, to2) = regexMatchedRows.find(columns[2].text())!!
                .groupValues.subList(1, 4)
            if (fileName1 != analysisFiles.fileName || fileName2 != analysisFiles.fileName)
                throw AnalysisException("JPlag does not support the analysis of files with the same name")
            matchedLines += (from1.toInt() to to1.toInt()) to (from2.toInt() to to2.toInt())
        }
        AnalysisResult(
            students = name1 to name2,
            countOfLines = -1,
            percentage = percentage,
            repository = analysisFiles.repoName,
            fileName = analysisFiles.fileName,
            matchedLines = matchedLines
        )
    }

    private fun rangeOfMatchIndexes(analysisFiles: PreparedAnalysisFiles): IntRange {
        val countOfMatches = (0 until analysisFiles.solutions.size).sum()
        return 0 until countOfMatches
    }
}