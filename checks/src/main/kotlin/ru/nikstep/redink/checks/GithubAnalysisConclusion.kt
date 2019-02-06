package ru.nikstep.redink.checks

enum class GithubAnalysisConclusion {
    SUCCESS,
    FAILURE,
    NEUTRAL,
    CANCELLED,
    TIMED_OUT,
    ACTION_REQUIRED;

    val value: String = this.name.toLowerCase()
}