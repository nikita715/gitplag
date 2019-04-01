package ru.nikstep.redink.core.view

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.util.RandomGenerator
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
    @Value("\${redink.analysisFilesDir}") private val analysisFilesDir: String
) {

    private val resultsStyle: HEAD.() -> Unit = {
        styleLink("/style.css")
        link(href = "https://fonts.googleapis.com/css?family=Roboto", rel = LinkRel.stylesheet)
    }

    @GetMapping("/", "repositories")
    fun getRepositories(): String {
        return createHTML().html {
            head {
                title("Repositories")
                apply(resultsStyle)
            }
            body {
                main {
                    ul {
                        repositoryDataManager.findAll().forEach { repo ->
                            li { a("analyzes?git=${repo.gitService.toString().toUpperCase()}&repoName=${repo.name}") { +"${repo.gitService} - ${repo.name}" } }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("analyzes")
    fun getRepoAnalyzes(@RequestParam("git") git: String, @RequestParam("repoName") repoName: String): String? {
        val repo = repositoryDataManager.findByGitServiceAndName(GitProperty.valueOf(git), repoName)
        if (repo == null) return null else {
            return createHTML().html {
                head {
                    title("$git/$repoName")
                    apply(resultsStyle)
                }
                body {
                    main {
                        ul {
                            repo.analyzes.forEach { analysis ->
                                li { a("analyzes/${analysis.id}") { +analysis.id.toString() } }
                            }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("analyzes/{analysisId}")
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
                        table {
                            analysis.analysisPairs.sortedWith(compareByDescending(AnalysisPair::percentage))
                                .forEach { pair ->
                                    tr {
                                        td {
                                            a("${analysis.id}/pair/${pair.id}") { +"${pair.student1}/${pair.student2}" }
                                        }
                                        td {
                                            +"${pair.percentage}%"
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("analyzes/{analysisId}/pair/{analysisPairId}")
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
                                var pairIndex = 0
                                val file1 =
                                    File("$analysisFilesDir/${analysis.hash}/${analysisPair.student1}").listFiles()[0]
                                file1.readLines().forEachIndexed { index, line ->
                                    val pair = analysisPair.analysisPairLines.getOrNull(pairIndex)
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
                        section("solution") {
                            pre {
                                var pairIndex = 0
                                val file2 =
                                    File("$analysisFilesDir/${analysis.hash}/${analysisPair.student2}").listFiles()[0]
                                file2.readLines().forEachIndexed { index, line ->
                                    val pair = analysisPair.analysisPairLines.getOrNull(pairIndex)
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