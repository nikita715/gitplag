package ru.nikstep.redink.github.data

enum class GithubAnalysisStatus {
    COMPLETED,
    IN_PROGRESS,
    QUEUED;

    val value: String = this.name.toLowerCase()
}