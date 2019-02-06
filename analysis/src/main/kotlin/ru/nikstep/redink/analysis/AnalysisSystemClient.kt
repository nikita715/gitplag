package ru.nikstep.redink.analysis

import java.io.File

interface AnalysisSystemClient {
    fun base(base: File): AnalysisSystemClient
    fun solutions(solutions: List<File>): AnalysisSystemClient
    fun analyse(): String?
}

