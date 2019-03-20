package ru.nikstep.redink.core.analysis

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.git.loader.GitLoader
import ru.nikstep.redink.git.webhook.PayloadProcessor
import ru.nikstep.redink.model.repo.BaseFileRecordRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SolutionFileRecordRepository
import ru.nikstep.redink.util.GitProperty

@RestController
class SolutionsController(
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val baseFileRecordRepository: BaseFileRecordRepository,
    private val repositoryRepository: RepositoryRepository,
    @Qualifier("gitLoaders") private val loaders: Map<GitProperty, GitLoader>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>
) {

    @GetMapping("/solutions")
    fun solutions(
        @RequestParam("git") git: GitProperty,
        @RequestParam("repo") repoName: String,
        @RequestParam("branch", required = false) branch: String?,
        @RequestParam("student", required = false) student: String?,
        @RequestParam("fileName", required = false) fileName: String?
    ): ResponseEntity<*> {
        val repo = repositoryRepository.findByGitServiceAndName(git, repoName)
            ?: return ResponseEntity.notFound().build<Any?>()
        return ResponseEntity.ok(solutionFileRecordRepository.findAllByRepo(repo)
            .filter { if (branch != null) it.branch == branch else true }
            .filter { if (student != null) it.user == student else true }
            .filter { if (fileName != null) it.fileName == fileName else true }
        )
    }


    @GetMapping("/solutions/import")
    fun importSolutions(
        @RequestParam("git") git: String,
        @RequestParam("repo") repoName: String
    ): ResponseEntity<*> {
        val gitProperty = GitProperty.valueOf(git.toUpperCase())
        val repository = repositoryRepository.findByGitServiceAndName(gitProperty, repoName)
            ?: return ResponseEntity.notFound().build<Any?>()
        val gitLoader = loaders.getValue(gitProperty)
        val payloadProcessor = payloadProcessors.getValue(gitProperty)
        gitLoader.cloneRepository(repository)
        payloadProcessor.downloadAllPullRequestsOfRepository(repository)
        return ResponseEntity.ok(
            mapOf(
                "bases" to baseFileRecordRepository.findAllByRepo(repository),
                "solutions" to solutionFileRecordRepository.findAllByRepo(repository)
            )
        )
    }
}
