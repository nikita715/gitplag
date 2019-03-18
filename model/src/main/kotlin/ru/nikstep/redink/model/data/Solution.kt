package ru.nikstep.redink.model.data

import java.io.File

class Solution(
    val student: String,
    val fileName: String,
    val file: File,
    val includedFileNames: List<String> = emptyList(),
    val includedFilePositions: List<Int> = emptyList(),
    val sha: String,
    val realFileName: String = ""
)

fun findSolutionByStudent(solutions: List<Solution>, student: String) =
    requireNotNull(solutions.find { it.student == student })
