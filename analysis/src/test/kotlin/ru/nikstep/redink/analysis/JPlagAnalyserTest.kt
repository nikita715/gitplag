package ru.nikstep.redink.analysis

import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines

class JPlagAnalyserTest : AbstractAnalyserTest() {

    override val analysisService =
        JPlagAnalyser(solutionStorageService, solutionsDir)

    override val expectedResult = listOf(
        AnalysisResult(
            students = "student2" to "student1",
            sha = sha2 to sha1,
            gitService = gitService,
            lines = -1,
            percentage = 100,
            repo = testRepoName,
            matchedLines = listOf(
                MatchedLines(
                    match1 = 13 to 22,
                    match2 = 12 to 21,
                    files = testFileName to testFileName
                )
            )
        ),
        AnalysisResult(
            students = "student3" to "student2",
            sha = sha3 to sha2,
            gitService = gitService,
            lines = -1,
            percentage = 55,
            repo = testRepoName,
            matchedLines = listOf(
                MatchedLines(
                    match1 = 10 to 18,
                    match2 = 15 to 22,
                    files = testFileName to testFileName
                )
            )
        ),
        AnalysisResult(
            students = "student3" to "student1",
            sha = sha3 to sha1,
            gitService = gitService,
            lines = -1,
            percentage = 55,
            repo = testRepoName,
            matchedLines = listOf(
                MatchedLines(
                    match1 = 10 to 18,
                    match2 = 14 to 21,
                    files = testFileName to testFileName
                )
            )
        )
    )
}