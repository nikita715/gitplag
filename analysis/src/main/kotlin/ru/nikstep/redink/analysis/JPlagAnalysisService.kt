package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import ru.nikstep.redink.util.inTempDirectory
import ru.nikstep.redink.util.onlyLastName
import java.io.File
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.math.roundToInt

class JPlagAnalysisService(private val solutionStorageService: SolutionStorageService) : AnalysisService {

    private val logger = KotlinLogging.logger {}

    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")
    private val solutionsPath = "solutions".asPathInRoot()
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()

    override fun analyse(pullRequest: PullRequest): Collection<AnalysisResult> =
        solutionStorageService.loadAllBasesAndSolutions(pullRequest)
            .flatMap { analysisFiles ->
                inTempDirectory { resultDir ->
                    executeJPlag(pullRequest, analysisFiles.fileName, resultDir)
                    rangeOfMatchIndexes(analysisFiles).map { index ->
                        toAnalysisResults(analysisFiles, resultDir)(index)
                    }
                }
            }


    private fun executeJPlag(pullRequest: PullRequest, fileName: String, resultDir: String) {
        val command = buildCommand(JPlagLang.JAVA_1_7, pullRequest.repoFullName, fileName, resultDir)
        Runtime.getRuntime().exec(command).waitFor(5, MINUTES)
    }

    private fun buildCommand(lang: JPlagLang, repoName: String, fileName: String, resultDir: String): String {
        return buildString {
            append("java -jar $jplagPath ")
            append("-l $lang ")
            append("-bc .base ")
            append("-r $resultDir ")
            append("-p ${fileName.onlyLastName()} ")
            append("-s ")
            append(asPath(solutionsPath, repoName))
        }
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