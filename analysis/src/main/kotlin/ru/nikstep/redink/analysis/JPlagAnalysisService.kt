package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.asFileInRoot
import ru.nikstep.redink.util.onlyLastName
import java.io.File
import java.nio.file.Files
import kotlin.math.roundToInt

class JPlagAnalysisService(private val solutionStorageService: SolutionStorageService) : AnalysisService {

    private val logger = KotlinLogging.logger {}

    private val separator = System.getProperty("file.separator")
    private val jplagPath = "${"libs".asFileInRoot()}${separator}jplag.jar"
    private val solutionsPath = "solutions".asFileInRoot()
    private val regexUserNames = "^Matches for (.+) & (.+)$".toRegex()
    private val regexMatchedRows = "^(.+)\\((\\d+)-(\\d+)\\)$".toRegex()


    override fun analyse(pullRequest: PullRequest): Collection<AnalysisResult> {
        return solutionStorageService.loadAllBasesAndSolutions(pullRequest).flatMap {
            val resultDir = Files.createTempDirectory(".res").toFile()
            val resultDirPath = resultDir.absolutePath
            val command = buildCommand(JPlagLang.JAVA_1_7, pullRequest.repoFullName, it.fileName, resultDirPath)
            println("exec $command")
            Runtime.getRuntime().exec(command).waitFor()
            println("exec success")

            val countOfSolutionFiles = it.solutions.size
            val countOfMatches = (0 until countOfSolutionFiles).sum()

            val list = mutableListOf<AnalysisResult>()

            for (numberOfMatch in 0 until countOfMatches) {
                val body = Jsoup.parse(File("$resultDirPath${separator}match$numberOfMatch-link.html").readText())
                    .body()
                val (name1, name2) = regexUserNames.find(body.getElementsByTag("H3")[0].text())!!
                    .groupValues.subList(1, 3)
                val percentage = body.getElementsByTag("H1")[0].text().replace("%", "").toDouble().roundToInt()
                val body2 = Jsoup.parse(File("$resultDirPath${separator}match$numberOfMatch-top.html").readText())
                    .body()
                val rows = body2.getElementsByTag("tr")
                val matchedLines = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
                for (rowNumber in 1 until rows.size - 1) {
                    val columns = rows[rowNumber].getElementsByTag("td")
                    val (fileName1, from1, to1) = regexMatchedRows.find(columns[1].text())!!.groupValues.subList(1, 4)
                    val (fileName2, from2, to2) = regexMatchedRows.find(columns[2].text())!!.groupValues.subList(1, 4)
                    if (fileName1 != it.fileName || fileName2 != it.fileName)
                        throw AnalysisException("JPlag does not support analysing files with the same name")
                    matchedLines += (from1.toInt() to to1.toInt()) to (from2.toInt() to to2.toInt())
                }
                list += AnalysisResult(
                    students = name1 to name2,
                    countOfLines = -1,
                    percentage = percentage,
                    repository = pullRequest.repoFullName,
                    fileName = it.fileName,
                    matchedLines = matchedLines
                )
            }
            resultDir.deleteRecursively()
            return list
        }
    }

    private fun buildCommand(lang: JPlagLang, repoName: String, fileName: String, resultDir: String): String {
        return buildString {
            append("java -jar $jplagPath ")
            append("-l $lang ")
            append("-bc .base ")
            append("-r $resultDir ")
            append("-p ${fileName.onlyLastName()} ")
            append("-s ")
            append("$solutionsPath$separator$repoName")
        }
    }
}