package ru.nikstep.redink.data

enum class GithubAnalysisStatus {
    COMPLETED,
    IN_PROGRESS,
    QUEUED;

    val value: String = this.name.toLowerCase()
}