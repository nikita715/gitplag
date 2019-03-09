package ru.nikstep.redink.analysis.loader

import org.junit.Test

class Test {
    @Test
    fun test() {
        println(Pair("nikita", "qwe").sort())
    }

    private fun Pair<String, String>.sort(): Pair<String, String> = if (first > second) Pair(second, first) else this

}