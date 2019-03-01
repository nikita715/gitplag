package ru.nikstep.redink.analysis

import ru.nikstep.redink.util.Language
import java.io.File

class PreparedAnalysisFiles(
    val repoName: String,
    val fileName: String,
    val language: Language,
    val base: File,
    val solutions: Collection<File>
)