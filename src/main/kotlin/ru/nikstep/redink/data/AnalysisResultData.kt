package ru.nikstep.redink.data

import ru.nikstep.redink.entity.GithubAnalysisConclusion
import ru.nikstep.redink.entity.GithubAnalysisStatus

class AnalysisResultData(
    val conclusion: GithubAnalysisConclusion = GithubAnalysisConclusion.SUCCESS,
    val status: GithubAnalysisStatus = GithubAnalysisStatus.COMPLETED,
    val detailsUrl: String = "https://localhost:8080",
    val summary: String = "Success"
)