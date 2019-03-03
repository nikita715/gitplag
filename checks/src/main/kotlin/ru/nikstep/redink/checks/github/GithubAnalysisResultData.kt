package ru.nikstep.redink.checks.github

/**
 * Result of the plagiarism analysis for a github check
 */
class GithubAnalysisResultData(
    val conclusion: String = GithubAnalysisConclusion.NEUTRAL.value,
    val status: String = GithubAnalysisStatus.IN_PROGRESS.value,
    val detailsUrl: String = "https://localhost:8080",
    val summary: String = "OK"
)