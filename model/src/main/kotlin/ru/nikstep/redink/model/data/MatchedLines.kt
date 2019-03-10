package ru.nikstep.redink.model.data

data class MatchedLines(
    val match1: Pair<Int, Int>,
    val match2: Pair<Int, Int>,
    val files: Pair<String, String>
)