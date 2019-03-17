package ru.nikstep.redink.model.data

import ru.nikstep.redink.util.GitProperty

class SourceFileInfo(
    val gitService: GitProperty,
    val mainRepoFullName: String,
    val sourceBranchName: String,
    val prNumber: Int,
    val fileName: String,
    val creator: String,
    val fileText: String,
    val mainBranchName: String,
    val headSha: String
)