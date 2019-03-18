package ru.nikstep.redink.core.analysis

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.analyser
import ru.nikstep.redink.model.data.language
import ru.nikstep.redink.model.data.mode
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty

/**
 * Analysis api controller
 */
@RestController
class AnalysisController(
    private val analysisRunner: AnalysisRunner,
    private val analysisRepository: AnalysisRepository,
    private val repositoryRepository: RepositoryRepository,
    private val analysisAsyncRunner: AnalysisAsyncRunner
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Receive analysis, wait and receive results
     */
    @GetMapping("/analysis/run")
    fun analysisLazy(
        @RequestParam("git") git: GitProperty,
        @RequestParam("repoName") repoName: String,
        @RequestParam("branches") branches: String,
        @RequestParam("analyser", required = false) analyser: String?,
        @RequestParam("language", required = false) language: String?,
        @RequestParam("mode", required = false) analysisMode: String?
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(git, repoName)
            ?: return ResponseEntity.notFound().build<Any?>()
        val analysisSettings =
            branches.split(",").map { branch ->
                AnalysisSettings(repository, requireNotNull(branch)).language(language).analyser(analyser)
                    .mode(analysisMode)
            }
        val results = analysisRunner.run(analysisSettings)
        return if (results.size == 1)
            ResponseEntity.ok(results[0])
        else
            ResponseEntity.ok(results)
    }

    /**
     * Receive analysis, find last executed analysis result
     */
    @GetMapping("/analysis")
    fun analysis(@RequestParam("git") git: GitProperty, @RequestParam("repoName") repoName: String): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(git, repoName)
            ?: return ResponseEntity.notFound().build<Any?>()
        val analysis = analysisRepository.findFirstByRepositoryOrderByExecutionDateDesc(repository)
        return if (analysis != null) ResponseEntity.ok(analysis) else ResponseEntity.notFound().build<Any?>()
    }

    /**
     * Receive analysis, run analysis silently, send results to [responseUrl]
     */
    @GetMapping("/analysis/trigger")
    fun analysisStatic(
        @RequestParam("git") git: GitProperty,
        @RequestParam("repoName") repoName: String,
        @RequestParam("branches") branches: String,
        @RequestParam("analyser", required = false) analyser: String?,
        @RequestParam("language", required = false) language: String?,
        @RequestParam("responseUrl", required = false) responseUrl: String?,
        @RequestParam("mode", required = false) analysisMode: String?
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(git, repoName)
            ?: return ResponseEntity.notFound().build<Any?>()
        val analysisSettings =
            branches.split(",").map { branch ->
                AnalysisSettings(repository, branch).language(language).analyser(analyser)
                    .mode(analysisMode)
            }
        analysisAsyncRunner.runAndRespond(analysisSettings, responseUrl)
        return ResponseEntity.ok("Accepted")
    }

}