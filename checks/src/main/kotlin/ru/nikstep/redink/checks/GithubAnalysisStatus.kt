package ru.nikstep.redink.checks

enum class GithubAnalysisStatus {
    COMPLETED,
    IN_PROGRESS,
    QUEUED;

    val value: String = this.name.toLowerCase()
}