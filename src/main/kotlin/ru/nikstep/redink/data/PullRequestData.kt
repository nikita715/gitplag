package ru.nikstep.redink.data

class PullRequestData(
    val number: Int,
    val installationId: Int,
    val creatorName: String,
    val repoOwnerName: String,
    val repoName: String,
    val repoFullName: String,
    val headSha: String,
    val branchName: String
)