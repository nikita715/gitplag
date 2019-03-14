package moss

import org.junit.Test
import java.io.File

class MossParsingTest {
    val text =
        File("/Users/nikstepmac/IdeaProjects/redink/model/src/test/resources/sources/cl.java").readText()

    val mossPairs1 = listOf(32 to 54, 19 to 25, 67 to 69)
    val mossPairs2 = listOf(1 to 23, 88 to 94, 47 to 49)
    val lengths1 = listOf(31, 30, 11)
    val files = listOf("file1", "file2", "file3")
    val lengths11 = listOf(31, 61, 72)
    val lengths2 = listOf(26, 26, 28, 20)

    @Test
    fun test() {
        println(mossPairs1.map {
            var index = 0
            for (i in lengths11) {
                if (it.first >= i) {
                    index++
                }
            }
            if (index > 0)
                files[index] to (it.first - lengths11[index - 1] to it.second - lengths11[index - 1]) else
                files[index] to it
        })
    }

    fun sum(list: List<Int>, int: Int): Int {
        return list.subList(0, int).sum()
    }

    fun index(list: List<Int>, int: Int): Int {
        for (i in 1..list.size) {
            if (list[i - 1] == int) return i
        }
        return -1
    }

}