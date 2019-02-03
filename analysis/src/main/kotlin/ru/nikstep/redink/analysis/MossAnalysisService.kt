package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import org.jsoup.Jsoup
import ru.nikstep.redink.data.AnalysisResult
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.model.repo.RepositoryRepository

class MossAnalysisService(
    private val solutionLoadingService: SolutionLoadingService,
    private val repositoryRepository: RepositoryRepository,
    private val mossId: String
) : AnalysisService {

    private val logger = KotlinLogging.logger {}

    override fun analyse(prData: PullRequestData): Set<AnalysisResult> {

        val result = mutableSetOf<AnalysisResult>()
        prData.changedFiles.forEach {
            val (_, list) = solutionLoadingService.loadSolutions(prData.repoFullName, it)

            val simpleMoss = SimpleMoss(
                mossId,
                "java",
                SocketClient(),
                emptyList(),
                list
            )
            val href = simpleMoss.analyse()
            if (href != null) {
                logger.info {
                    "Analysis: for repo ${prData.repoFullName}, user ${prData.creatorName}," +
                            " file $it, url $href "
                }

                val set = Jsoup.connect(href).get()
                    .body()
                    .getElementsByTag("table")
                    .select("tr")
                    .drop(1)
                    .map { tr -> tr.select("td") }
                    .map { tds ->
                        val first = tds[0].selectFirst("a")
                        val second = tds[1].selectFirst("a")

                        val link = first.attr("href")

                        val firstPath = first.text().split(" ").first().split("/")
                        val secondPath = second.text().split(" ").first().split("/")

                        val students =
                            firstPath.zip(secondPath)
                                .dropWhile { it.first == it.second }
                                .first()

                        val lines = tds[2].text().toInt()

                        val percentage = first.text().split(" ")
                            .last()
                            .removeSurrounding("(", "%)")
                            .toInt()

                        val rows = Jsoup.connect(link.replace(".html", "-top.html"))
                            .get().getElementsByTag("tr")
                        val matchedLines = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
                        for (row in rows.subList(1, rows.size)) {
                            val cells = row.getElementsByTag("td")
                            val data = cells[0].selectFirst("a").text().split("-")
                            val data2 = cells[2].selectFirst("a").text().split("-")
                            matchedLines.add((data[0].toInt() to data[1].toInt()) to (data2[0].toInt() to data2[1].toInt()))
                        }

                        AnalysisResult(students, lines, percentage, matchedLines)
                    }
                    .toSet()
                result.addAll(set)
            }
        }
        return result
    }
}