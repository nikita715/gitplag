package ru.nikstep.redink.entity

import java.net.URL

class AnalysisResult(
    val url: URL,
    val students: List<String>,
    val matches: Set<AnalysisMatch>
)