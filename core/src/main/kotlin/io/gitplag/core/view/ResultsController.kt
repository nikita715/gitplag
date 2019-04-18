package io.gitplag.core.view

import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.AnalysisPair
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.AnalysisPairRepository
import io.gitplag.model.repo.AnalysisRepository
import io.gitplag.util.RandomGenerator
import io.gitplag.util.innerRegularFiles
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.File

/**
 * Analysis results views controller
 */
@RestController
class ResultsController(
    private val analysisRepository: AnalysisRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val analysisPairRepository: AnalysisPairRepository,
    private val randomGenerator: RandomGenerator,
    @Value("\${gitplag.analysisFilesDir}") private val analysisFilesDir: String,
    @Value("\${gitplag.graphUrl}") private val graphUrl: String,
    @Value("\${gitplag.serverUrl}") private val serverUrl: String
) {

    private val resultsStyle: HEAD.() -> Unit = {
        styleLink("/results.css")
        link(href = "https://fonts.googleapis.com/css?family=Roboto", rel = LinkRel.stylesheet)
    }

    @GetMapping("/")
    fun getRepositories(): String {
        return createHTML().html {
            head {
                title("Repositories")
                apply(resultsStyle)
            }
            body {
                main {
                    h3 { +"Repositories" }
                    ul {
                        repositoryDataManager.findAll().forEach { repo ->
                            li {
                                a("/repository/${repo.id}")
                                { +"${repo.gitService} - ${repo.name}" }
                            }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("repository/{repositoryId}")
    fun getRepoAnalyzes(@PathVariable repositoryId: Long): String? {
        val repo = repositoryDataManager.findById(repositoryId)
        if (repo == null) return null else {
            return createHTML().html {
                head {
                    title(repo.run { "$gitService/$name" })
                    apply(resultsStyle)
                }
                body {
                    main {
                        h3 { +"Analyzes of ${repo.gitService} repository ${repo.name}" }
                        ul {
                            repo.analyzes.forEach { analysis ->
                                li { a("/analysis/${analysis.id}") { +analysis.id.toString() } }
                            }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("analysis/{analysisId}")
    fun getAnalysis(@PathVariable analysisId: Long): String? {
        val analysis = analysisRepository.findById(analysisId).orElse(null)
        if (analysis == null) return null else {
            return createHTML().html {
                head {
                    title("Analysis #$analysisId")
                    apply(resultsStyle)
                }
                body {
                    main {
                        h3 { +"Analysis #$analysisId of ${analysis.repository.gitService} repository ${analysis.repository.name}" }
                        a(href = buildAnalysisGraphUrl(analysisId)) { +"Graph" }
                        table {
                            thead {
                                tr {
                                    th { +"Id" }
                                    th { +"First match" }
                                    th { +"Second match" }
                                    th { +"Percentage" }
                                }
                            }
                            tbody {
                                analysis.analysisPairs.sortedWith(compareByDescending(AnalysisPair::percentage))
                                    .forEach { pair ->
                                        tr {
                                            td {
                                                a("/analysis/${analysis.id}/pair/${pair.id}") { +pair.id.toString() }
                                            }
                                            td { +pair.student1 }
                                            td { +pair.student2 }
                                            td { +pair.percentage.toString() }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildAnalysisGraphUrl(analysisId: Long) = "$graphUrl$serverUrl/graph/$analysisId"

    @GetMapping("analysis/{analysisId}/pair/{analysisPairId}")
    fun getAnalysis(@PathVariable analysisId: Long, @PathVariable analysisPairId: Long): String? {
        val analysis = analysisRepository.findById(analysisId).orElse(null)
        val analysisPair = analysisPairRepository.findById(analysisPairId).orElse(null)
        if (analysisPair == null) return null else {
            return createHTML().html {
                head {
                    title("Result #${analysisPair.id}")
                    apply(resultsStyle)
                }
                body {
                    header("solution-compare-header") {
                        span("student-name") { +analysisPair.student1 }
                        span("student-name") { +analysisPair.student2 }
                    }
                    main("solution-compare") {
                        val colors = analysisPair.analysisPairLines.map { randomGenerator.randomHexColor() }
                        section("solution") {
                            pre {
                                val files1 = findAnalysisFiles(analysis, analysisPair.student1)
                                val leftAnalysisPairLines = analysisPair.analysisPairLines.sortedBy { it.fileName1 }
                                files1.forEach { file ->
                                    var pairIndex = 0
                                    file.readLines().forEachIndexed { index, line ->
                                        val pair = leftAnalysisPairLines.getOrNull(pairIndex)
                                        if (pair != null && index == pair.from1 - 1) {
                                            unsafe { +"<font color=\"${colors[pairIndex]}\">\n" }
                                        } else if (pair != null && index == pair.to1 - 1) {
                                            pairIndex++
                                            unsafe { +"</font>\n" }
                                        }

                                        +line
                                        +"\n"
                                    }
                                }
                            }
                        }
                        section("solution") {
                            pre {
                                val files2 = findAnalysisFiles(analysis, analysisPair.student2)
                                val rightAnalysisPairLines = analysisPair.analysisPairLines.sortedBy { it.fileName2 }
                                files2.forEach { file ->
                                    var pairIndex = 0
                                    file.readLines().forEachIndexed { index, line ->
                                        val pair = rightAnalysisPairLines.getOrNull(pairIndex)
                                        if (pair != null && index == pair.from2 - 1) {
                                            unsafe { +"<font color=\"${colors[pairIndex]}\">\n" }
                                        } else if (pair != null && index == pair.to2 - 1) {
                                            pairIndex++
                                            unsafe { +"</font>\n" }
                                        }
                                        +line
                                        +"\n"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun findAnalysisFiles(analysis: Analysis, user: String): List<File> =
        when (analysis.analyzer) {
            AnalyzerProperty.MOSS -> listOf(File("$analysisFilesDir/${analysis.hash}/$user").listFiles()[0])
            AnalyzerProperty.JPLAG -> File("$analysisFilesDir/${analysis.hash}/$user").innerRegularFiles()
        }

}