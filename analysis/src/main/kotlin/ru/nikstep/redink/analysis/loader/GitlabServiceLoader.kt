package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.repo.RepositoryRepository

class GitlabServiceLoader(
    solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository
) : AbstractGitServiceLoader(solutionStorage, repositoryRepository) {

    override fun getFileQuery(repoName: String, branchName: String, fileName: String): String {
        return "https://gitlab.com/$repoName/raw/$branchName/$fileName"
    }
}