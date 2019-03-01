package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest

class MossAnalysisService(
    private val solutionStorage: SolutionStorage,
    private val mossId: String
) : AnalysisService {

    private val logger = KotlinLogging.logger {}

    override fun analyse(pullRequest: PullRequest): Collection<AnalysisResult> {
        return solutionStorage.loadAllBasesAndSolutions(pullRequest).flatMap { analysisFiles ->
            val resultLink = MossClient(mossId, analysisFiles).analyse()

            logger.info {
                "Analysis: for repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}," +
                        " file ${analysisFiles.fileName}, url $resultLink "
            }

            createAnalysisResults(pullRequest, resultLink, analysisFiles.fileName)
        }
    }

    private fun createAnalysisResults(
        pullRequest: PullRequest,
        resultLink: String,
        fileName: String
    ): Collection<AnalysisResult> {
        return Jsoup.connect(resultLink).get()
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

                if (pullRequest.creatorName != students.first && pullRequest.creatorName != students.second)
                    return@mapNotNull null

                val lines = tds[2].text().toInt()

                val percentage = first.text().split(" ")
                    .last()
                    .removeSurrounding("(", "%)")
                    .toInt()

                val rows = Jsoup.connect(first.attr("href").replace(".html", "-top.html"))
                    .get().getElementsByTag("tr")
                val matchedLines = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
                for (row in rows.subList(1, rows.size)) {
                    val cells = row.getElementsByTag("td")
                    val firstMatch = cells[0].selectFirst("a").text().split("-")
                    val secondMatch = cells[2].selectFirst("a").text().split("-")
                    matchedLines.add((firstMatch[0].toInt() to firstMatch[1].toInt()) to (secondMatch[0].toInt() to secondMatch[1].toInt()))
                }

                AnalysisResult(
                    students = students,
                    countOfLines = lines,
                    percentage = percentage,
                    repository = pullRequest.repoFullName,
                    fileName = fileName,
                    matchedLines = matchedLines
                )
            }
    }
}