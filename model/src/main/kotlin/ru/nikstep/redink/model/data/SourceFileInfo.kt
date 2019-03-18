package ru.nikstep.redink.model.data

import ru.nikstep.redink.model.entity.Repository

class SourceFileInfo(
    val repo: Repository,
    val sourceBranchName: String,
    val prNumber: Int,
    val fileName: String,
    val creator: String,
    val fileText: String,
    val mainBranchName: String,
    val headSha: String
)