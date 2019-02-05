package ru.nikstep.redink.analysis

import java.io.File

interface AnalysisSystemClient {
    fun base(bases: List<File>): AnalysisSystemClient
    fun solutions(solutions: List<File>): AnalysisSystemClient
    fun analyse(): String?
}

