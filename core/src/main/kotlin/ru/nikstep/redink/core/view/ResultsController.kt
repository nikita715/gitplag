package ru.nikstep.redink.core.view

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.util.RandomGenerator
import ru.nikstep.redink.util.innerRegularFiles
import java.io.File
import javax.servlet.http.HttpServletResponse

/**
 * Analysis results views controller
 */
@RestController
class ResultsController(
    private val analysisRepository: AnalysisRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val analysisPairRepository: AnalysisPairRepository,
    private val randomGenerator: RandomGenerator,
    @Value("\${redink.analysisFilesDir}") private val analysisFilesDir: String,
    @Value("\${redink.graphUrl}") private val graphUrl: String,
    @Value("\${server.port}") private val serverPort: String
) {

    private val resultsStyle: HEAD.() -> Unit = {
        styleLink("/style.css")
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

    private fun buildAnalysisGraphUrl(analysisId: Long) = "${graphUrl}http://localhost:$serverPort/graph/$analysisId"

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
                                        if (pair != null && index == pair.from1) {
                                            unsafe { +"<font color=\"${colors[pairIndex]}\">\n" }
                                        } else if (pair != null && index == pair.to1) {
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
                                        if (pair != null && index == pair.from2) {
                                            unsafe { +"<font color=\"${colors[pairIndex]}\">\n" }
                                        } else if (pair != null && index == pair.to2) {
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

    @GetMapping("analyzes/{analysisId}/pair")
    fun getAnalysisPair(
        @RequestParam student1: String, @RequestParam student2: String, @PathVariable analysisId: Long, response: HttpServletResponse
    ): String? {
        val analysisPair = analysisPairRepository.findByAnalysisIdAndStudent1AndStudent2(analysisId, student1, student2)
        if (analysisPair != null) {
            response.sendRedirect("/analysis/$analysisId/pair/${analysisPair.id}")
        }
        return null
    }

    private fun findAnalysisFiles(analysis: Analysis, user: String): List<File> =
        when (analysis.analyser) {
            AnalyserProperty.MOSS -> listOf(File("$analysisFilesDir/${analysis.hash}/$user").listFiles()[0])
            AnalyserProperty.JPLAG -> File("$analysisFilesDir/${analysis.hash}/$user").innerRegularFiles()
        }

}