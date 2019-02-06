package ru.nikstep.redink.checks

class AnalysisResultData(
    val conclusion: String = GithubAnalysisConclusion.NEUTRAL.value,
    val status: String = GithubAnalysisStatus.IN_PROGRESS.value,
    val detailsUrl: String = "https://localhost:8080",
    val summary: String = "OK"
)