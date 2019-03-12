package ru.nikstep.redink.analysis

import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines

class JPlagAnalyserTest : AbstractAnalyserTest() {

    override val analysisService =
        JPlagAnalyser(solutionStorageService, solutionsDir)

    override val expectedResult = listOf(
        AnalysisResult(
            gitService = gitService,
            repo = testRepoName,
            resultLink = "",
            matchData = listOf(
                AnalysisMatch(
                    students = "student2" to "student1",
//            sha = sha2 to sha1,
                    sha = "" to "",
                    lines = -1,
                    percentage = 100,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 13 to 22,
                            match2 = 12 to 21,
                            files = testFileName to testFileName
                        )
                    )
                )
            )
        ),
        AnalysisResult(
            repo = testRepoName,
            gitService = gitService,
            resultLink = "",
            matchData = listOf(
                AnalysisMatch(
                    students = "student3" to "student2",
//            sha = sha3 to sha2,
                    sha = "" to "",
                    lines = -1,
                    percentage = 55,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 10 to 18,
                            match2 = 15 to 22,
                            files = testFileName to testFileName
                        )
                    )
                )
            )
        ),
        AnalysisResult(
            repo = testRepoName,
            gitService = gitService,
            resultLink = "",
            matchData = listOf(
                AnalysisMatch(
//            sha = sha3 to sha1,
                    sha = "" to "",
                    students = "student3" to "student1",
                    lines = -1,
                    percentage = 55,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 10 to 18,
                            match2 = 14 to 21,
                            files = testFileName to testFileName
                        )
                    )
                )
            )
        )
    )
}