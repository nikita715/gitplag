package ru.nikstep.redink.analysis

import ru.nikstep.redink.util.asPath
import java.io.File

abstract class AbstractAnalyserTest {

    private val separateSolutionsDir = asPath("src", "test", "resources", "separateSolutions")

    protected val student1 = "student1"
    protected val student2 = "student2"
    protected val student3 = "student3"
    protected val file1Name = "file1.java"
    protected val file2Name = "file2.java"
    protected val file3Name = "file3.java"
    protected val file4Name = "file4.java"
    protected val file5Name = "file5.java"
    protected val file6Name = "file6.java"
    protected val file7Name = "file7.java"
    protected val file8Name = "file8.java"
    protected val file9Name = "file9.java"

    protected val base1 =
        File("$separateSolutionsDir/.base/base1.java")
    protected val base2 =
        File("$separateSolutionsDir/.base/base2.java")

    protected val sha1 = "sha1"
    protected val sha2 = "sha2"
    protected val sha3 = "sha3"
}