package ru.nikstep.redink.data

class Match(
    val file1Id: Long,
    val file2Id: Long,
    val count: Int,
    val matches: List<Pair<Int, Int>>
)