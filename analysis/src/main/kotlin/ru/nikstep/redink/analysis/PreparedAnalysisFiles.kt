package ru.nikstep.redink.analysis

import java.io.File

class PreparedAnalysisFiles(
    val fileName: String,
    val base: File,
    val solutions: Collection<File>
)