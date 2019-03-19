package ru.nikstep.redink.core.analysis

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.git.loader.GitLoader
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SolutionFileRecordRepository
import ru.nikstep.redink.util.GitProperty

@RestController
class SolutionsController(
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val repositoryRepository: RepositoryRepository,
    @Qualifier("gitLoaders") private val loaders: Map<GitProperty, GitLoader>
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
        return ResponseEntity.ok(loaders.getValue(gitProperty).loadRepositoryAndPullRequestFiles(repository))
    }

}