package ru.nikstep.redink.service

import ru.nikstep.redink.repo.PullRequestRepository

class PullRequestLoadingService(
    val pullRequestRepository: PullRequestRepository,
    val plagiarismService: PlagiarismService
) {

    fun processPullRequests() {
        val pullRequests = pullRequestRepository.findAll()
        pullRequests.forEach {
            plagiarismService.analyze(it)
            pullRequestRepository.delete(it)
        }
    }

}
