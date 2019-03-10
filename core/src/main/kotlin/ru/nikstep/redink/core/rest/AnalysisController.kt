package ru.nikstep.redink.core.rest

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.analysis.AnalysisManager
import ru.nikstep.redink.analysis.AnalysisSettings
import ru.nikstep.redink.analysis.analyser
import ru.nikstep.redink.analysis.language
import ru.nikstep.redink.core.exceptionAtAnalysisOf
import ru.nikstep.redink.core.loggedAnalysis
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty

@RestController
class AnalysisController(
    private val analysisManager: AnalysisManager,
    private val analysisRepository: AnalysisRepository,
    private val repositoryRepository: RepositoryRepository
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/analysis/run")
    fun analysisLazy(
        @RequestParam("git") git: String,
        @RequestParam("repoName") repoName: String,
        @RequestParam("analyser", required = false) analyser: String,
        @RequestParam("language", required = false) language: String
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysisSettings = AnalysisSettings(repository).language(language).analyser(analyser)
        return try {
            val analysis = analysisManager.initiateAnalysis(analysisSettings)
            ResponseEntity.ok(logger.loggedAnalysis(repository) {
                analysisRepository.findById(analysis.id).get()
            })
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, repository)
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/analysis")
    fun analysis(@RequestParam("git") git: String, @RequestParam("repoName") repoName: String): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysis = analysisRepository.findFirstByRepositoryOrderByExecutionDateDesc(repository)
        return if (analysis != null) ResponseEntity.ok(analysis) else ResponseEntity.ok("Not analyzed")
    }

    @GetMapping("/analysis/trigger")
    fun analysisStatic(
        @RequestParam("git") git: String,
        @RequestParam("repoName") repoName: String,
        @RequestParam("analyser", required = false) analyser: String,
        @RequestParam("language", required = false) language: String
    ): ResponseEntity<*> {
        val repository = repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(git.toUpperCase()), repoName)
        val analysisSettings = AnalysisSettings(repository).language(language).analyser(analyser)
        run(analysisSettings)
        return ResponseEntity<Any>(HttpStatus.ACCEPTED)
    }

    @Async("analysisThreadPoolTaskExecutor")
    fun run(analysisSettings: AnalysisSettings) {
        analysisManager.initiateAnalysis(analysisSettings)
    }

}