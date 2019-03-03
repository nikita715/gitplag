package ru.nikstep.redink.checks.github

/**
 * State of a github check
 */
enum class GithubAnalysisStatus {
    COMPLETED,
    IN_PROGRESS,
    QUEUED;

    val value: String = this.name.toLowerCase()
}