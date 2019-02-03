package ru.nikstep.redink.analysis

import java.io.File

interface Moss {
    val userId: String
    val language: String

    fun base(bases: List<File>): Moss

    fun solutions(solutions: List<File>): Moss

    fun analyse(): String
}

