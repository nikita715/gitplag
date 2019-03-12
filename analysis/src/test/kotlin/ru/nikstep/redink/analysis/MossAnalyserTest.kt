package ru.nikstep.redink.analysis

import io.kotlintest.matchers.shouldEqual
import org.junit.Test
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines

class MossAnalyserTest : AbstractAnalyserTest() {

    override val analysisService =
        MossAnalyser(solutionStorageService, System.getenv("MOSS_ID"))

    override val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            gitService = gitService,
            resultLink = "",
            matchData = listOf(
                AnalysisMatch(

                    students = "student1" to "student2",
//            sha = sha1 to sha2,
                    sha = "" to "",
                    lines = 14,
                    percentage = 66,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 7 to 19,
                            match2 = 7 to 20,
//                    files = testFileName to testFileName
                            files = "" to ""
                        )
                    )
                ), AnalysisMatch(

                    students = "student2" to "student3",
//            sha = sha2 to sha3,
                    sha = "" to "",
                    lines = 5,
                    percentage = 22,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 16 to 20,
                            match2 = 12 to 16,
//                    files = testFileName to testFileName
                            files = "" to ""
                        )
                    )
                ), AnalysisMatch(
                    students = "student1" to "student3",
//            sha = sha1 to sha3,
                    sha = "" to "",
                    lines = 5,
                    percentage = 22,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 15 to 19,
                            match2 = 12 to 16,
//                    files = testFileName to testFileName
                            files = "" to ""
                        )
                    )
                )
            )

        )

    @Test
    fun analyse() {
        val analysisResult = analysisService.analyse(analysisSettings)
        analysisResult shouldEqual expectedResult.copy(resultLink = analysisResult.resultLink)
    }
}