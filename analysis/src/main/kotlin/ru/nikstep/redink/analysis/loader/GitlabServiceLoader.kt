package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.repo.RepositoryRepository

class GitlabServiceLoader(
    solutionStorageService: SolutionStorageService,
    repositoryRepository: RepositoryRepository
) : AbstractGitServiceLoader(solutionStorageService, repositoryRepository) {
    override fun getFileQuery(repoName: String, branchName: String, fileName: String): String {
        return "https://gitlab.com/$repoName/raw/$branchName/$fileName"
    }
}