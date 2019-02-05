package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.http.HttpMethod
import ru.nikstep.redink.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.RequestUtil
import ru.nikstep.redink.util.auth.AuthorizationService

class MossAnalysisService(
    private val sourceCodeService: SourceCodeService,
    private val repositoryRepository: RepositoryRepository,
    private val authorizationService: AuthorizationService,
    private val mossId: String
) : AnalysisService {

    private val rawGithubFileQuery = "{\"query\": \"query {repository(name: \\\"%s\\\", owner: \\\"%s\\\")" +
            " {object(expression: \\\"%s:%s\\\") {... on Blob{text}}}}\"}"

    private val logger = KotlinLogging.logger {}

    override fun analyse(prData: PullRequest): Set<AnalysisResult> {

        loadFiles(prData)

        val result = mutableSetOf<AnalysisResult>()
        prData.changedFiles.forEach {
            val (_, list) = sourceCodeService.load(prData.repoFullName, it)

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

                        AnalysisResult(students, lines, percentage, prData.repoFullName, matchedLines)
                    }
                    .toSet()
                result.addAll(set)
            }
        }
        return result
    }

    private fun loadFiles(data: PullRequest) {
        val fileNames = repositoryRepository.findByName(data.repoFullName).filePatterns

        for (fileName in fileNames) {
            val fileResponse = RequestUtil.sendGraphqlRequest(
                httpMethod = HttpMethod.POST,
                body = java.lang.String.format(
                    rawGithubFileQuery,
                    data.repoName,
                    data.repoOwnerName,
                    data.branchName,
                    fileName
                ),
                accessToken = authorizationService.getAuthorizationToken(data.installationId)
            )

            val resultObject = fileResponse.getJSONObject("data").getJSONObject("repository")

            if (resultObject.isNull("object")) {
                logger.error { "$fileName is not found in ${data.branchName} branch, ${data.repoFullName} repo" }
            } else {
                sourceCodeService.save(
                    data, fileName,
                    resultObject.getJSONObject("object").getString("text")
                )
            }
        }
    }
}