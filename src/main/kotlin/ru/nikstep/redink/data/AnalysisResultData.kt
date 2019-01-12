package ru.nikstep.redink.data

class AnalysisResultData(
    val conclusion: GithubAnalysisConclusion = GithubAnalysisConclusion.SUCCESS,
    val status: GithubAnalysisStatus = GithubAnalysisStatus.COMPLETED,
    val detailsUrl: String = "https://localhost:8080",
    val summary: String = "Success"
)