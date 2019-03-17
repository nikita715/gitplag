package ru.nikstep.redink.core.analysis

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.model.dto.RepositoryDto
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalysisMode
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language

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
    fun updateRepository(@RequestBody repositoryDto: RepositoryDto): Repository {
        val repository =
            repositoryRepository.findByGitServiceAndName(repositoryDto.gitService, repositoryDto.fullName)
        val repositoryCopy = repository.copy(
            language = repositoryDto.language ?: repository.language,
            filePatterns = repositoryDto.filePatterns ?: repository.filePatterns,
            analyser = repositoryDto.analyser ?: repository.analyser,
            periodicAnalysis = repositoryDto.periodicAnalysis ?: repository.periodicAnalysis,
            periodicAnalysisDelay = repositoryDto.periodicAnalysisDelay ?: repository.periodicAnalysisDelay,
            branches = repositoryDto.branches ?: repository.branches,
            analysisMode = repositoryDto.analysisMode ?: repository.analysisMode
        )
        return repositoryRepository.save(repositoryCopy)
    }

}
