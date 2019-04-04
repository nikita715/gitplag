package ru.nikstep.redink.model.data

import java.io.File
import java.time.LocalDateTime

/**
 * The class that contains the information about a solution file
 */
class Solution(
    val student: String,
    val fileName: String,
    val file: File,
    val sha: String,
    val createdAt: LocalDateTime
)

/**
 * Get a solution from [solutions] by [student] name
 */
fun findSolutionByStudent(solutions: List<Solution>, student: String) =
    requireNotNull(solutions.find { it.student == student })
