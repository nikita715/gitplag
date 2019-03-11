package ru.nikstep.redink.core.analysis

import com.github.kittinunf.fuel.core.Method
import mu.KotlinLogging
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.analysis.data.AnalysisSettings
import ru.nikstep.redink.analysis.data.analyser
import ru.nikstep.redink.analysis.data.language
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.sendRestRequest

/**
 * Analysis api controller
 */
@RestController
class AnalysisController(
    private val analysisRunner: AnalysisRunner,
    private val analysisRepository: AnalysisRepository,
    private val repositoryRepository: RepositoryRepository
) {
    private val logger = KotlinLogging.logger {}
    private val objectMapper = ObjectMapper()

    /**
     * Receive analysis, wait and receive results
     */
    @GetMapping("/analysis/run")
    fun analysisLazy(
        @RequestParam("git") git: String,
        @RequestParam("repoName") repoName: String,
        @RequestParam("analyser", required = false) analyser: String?,
        @RequestParam("language", required = false) language: String?
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysisSettings = AnalysisSettings(repository).language(language).analyser(analyser)
        return try {
            val analysis = analysisRunner.run(analysisSettings)
            ResponseEntity.ok(logger.loggedAnalysis(repository) {
                analysisRepository.findById(analysis.id).get()
            })
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, repository)
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
        @RequestParam("responseUrl", required = false) responseUrl: String?
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysisSettings = AnalysisSettings(repository).language(language).analyser(analyser)
        run(analysisSettings, responseUrl)
        return ResponseEntity<Any>(HttpStatus.ACCEPTED)
    }

    /**
     * Async runner of analyzes
     */
    @Async("analysisThreadPoolTaskExecutor")
    fun run(analysisSettings: AnalysisSettings, responseUrl: String?) {
        val analysis = analysisRunner.run(analysisSettings)
        if (responseUrl != null) sendRestRequest<Any>(
            url = responseUrl,
            method = Method.POST,
            body = objectMapper.writeValueAsString(analysis)
        )
    }

}