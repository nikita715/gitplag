package ru.nikstep.redink.util

import java.io.File

private val beforePattern = """[({}<>,.:=&|^\-+%/ ]""".toRegex();
private val afterPattern = """[){}<>,.:=&|^\-+%/ ]""".toRegex();

fun parseLines(fileName: String): List<String> {
    val lines = mutableListOf<String>()
    File(fileName).useLines { lines.addAll(it) }
    return replaceNoise(lines)
}

private fun replaceNoise(lines: MutableList<String>): List<String> {
    lines.replaceAll { it.trim() }
    lines.replaceAll(removeIrrelevantLines)
    lines.removeIf { it.equals("") || it.startsWith("import ") || it.startsWith("package ") }
    return lines
}

private val removeIrrelevantLines = { it: String ->
    val result: String
    if (it.length < 3) {
        result = it
    } else {
        val newString = StringBuilder()
        var isCurrString = false
        val stringArray = it.split("")

        var prev: String
        var curr = stringArray[0]
        var next = stringArray[1]

        newString.append(curr)

        for (i in 2 until stringArray.size) {
            prev = curr
            curr = next
            next = stringArray[i]

            if (curr.equals("\"") || curr.equals("\'")) isCurrString = !isCurrString

            if ((curr.equals(" ")
                        && !isCurrString && (beforePattern.matches(next) || afterPattern.matches(prev)))
            ) {
                continue
            }
            newString.append(curr)
        }
        result = newString.toString()
    }
    result
}
