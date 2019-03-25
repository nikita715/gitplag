package ru.nikstep.redink.core.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.nikstep.redink.model.dto.RepositoryDto
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.model.repo.RepositoryRepository

/**
 * Repository api controller
 */
@RestController
@RequestMapping("/repository")
class RepositoryController(
    private val repositoryRepository: RepositoryRepository
) {

    @GetMapping
    fun getRepository(
        @RequestParam("git") git: GitProperty,
        @RequestParam("repo") repoName: String
    ): Repository? = repositoryRepository.findByGitServiceAndName(git, repoName)

    @PostMapping
    fun createRepository(@RequestBody repositoryDto: RepositoryDto): Repository =
        repositoryDto.run {
            repositoryRepository.save(
                Repository(
                    name = fullName,
                    gitService = gitService,
                    language = language ?: Language.JAVA,
                    filePatterns = filePatterns ?: emptyList(),
                    analyser = analyser ?: AnalyserProperty.MOSS,
                    periodicAnalysis = periodicAnalysis ?: false,
                    periodicAnalysisDelay = periodicAnalysisDelay ?: 10,
                    branches = branches ?: emptyList(),
                    analysisMode = analysisMode ?: AnalysisMode.PAIRS
                )
            )
        }

    @PutMapping
    fun updateRepository(@RequestBody repositoryDto: RepositoryDto): ResponseEntity<*> {
        val repository =
            repositoryRepository.findByGitServiceAndName(repositoryDto.gitService, repositoryDto.fullName)
                ?: return ResponseEntity.notFound().build<Any?>()
        val repositoryCopy = repository.copy(
            language = repositoryDto.language ?: repository.language,
            filePatterns = repositoryDto.filePatterns ?: repository.filePatterns,
            analyser = repositoryDto.analyser ?: repository.analyser,
            periodicAnalysis = repositoryDto.periodicAnalysis ?: repository.periodicAnalysis,
            periodicAnalysisDelay = repositoryDto.periodicAnalysisDelay ?: repository.periodicAnalysisDelay,
            branches = repositoryDto.branches ?: repository.branches,
            analysisMode = repositoryDto.analysisMode ?: repository.analysisMode
        )
        return ResponseEntity.ok(repositoryRepository.save(repositoryCopy))
    }

}
