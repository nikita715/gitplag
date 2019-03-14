package ru.nikstep.redink.core.analysis

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.analyser
import ru.nikstep.redink.model.data.language
import ru.nikstep.redink.model.data.withLines
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
        @RequestParam("git") git: String,
        @RequestParam("repoName") repoName: String,
        @RequestParam("analyser", required = false) analyser: String?,
        @RequestParam("language", required = false) language: String?,
        @RequestParam("branch", required = false) branch: String?,
        @RequestParam("withLines", required = false) withLines: Boolean = false
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysisSettings =
            AnalysisSettings(repository, requireNotNull(branch)).language(language).analyser(analyser)
                .withLines(withLines)
        return try {
            val analysis = analysisRunner.run(analysisSettings)
            ResponseEntity.ok(logger.loggedAnalysis(analysisSettings) {
                analysisRepository.findById(analysis.id).get()
            })
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, analysisSettings)
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Receive analysis, find last executed analysis result
     */
    @GetMapping("/analysis")
    fun analysis(@RequestParam("git") git: String, @RequestParam("repoName") repoName: String): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysis = analysisRepository.findFirstByRepositoryOrderByExecutionDateDesc(repository)
        return if (analysis != null) ResponseEntity.ok(analysis) else ResponseEntity.ok("Not analyzed")
    }

    /**
     * Receive analysis, run analysis silently, send results to [responseUrl]
     */
    @GetMapping("/analysis/trigger")
    fun analysisStatic(
        @RequestParam("git") git: String,
        @RequestParam("repoName") repoName: String,
        @RequestParam("analyser", required = false) analyser: String?,
        @RequestParam("language", required = false) language: String?,
        @RequestParam("branch", required = false) branch: String?,
        @RequestParam("withLines", required = false) withLines: Boolean = false,
        @RequestParam("responseUrl", required = false) responseUrl: String?
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysisSettings =
            AnalysisSettings(repository, requireNotNull(branch)).language(language).analyser(analyser)
                .withLines(withLines)
        analysisAsyncRunner.runAndRespond(analysisSettings, responseUrl)
        return ResponseEntity.ok("Accepted")
    }

}