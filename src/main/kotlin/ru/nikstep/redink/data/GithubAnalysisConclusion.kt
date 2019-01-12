package ru.nikstep.redink.data

enum class GithubAnalysisConclusion {
    SUCCESS,
    FAILURE,
    NEUTRAL,
    CANCELLED,
    TIMED_OUT,
    ACTION_REQUIRED;

    val value: String = this.name.toLowerCase()
}