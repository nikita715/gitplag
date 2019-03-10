package ru.nikstep.redink.analysis

import org.junit.Ignore
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines

@Ignore
class MossAnalyserTest : AbstractAnalyserTest() {

    override val analysisService =
        MossAnalyser(solutionStorageService, System.getenv("MOSS_ID"))

    override val expectedResult = listOf(
        AnalysisResult(
            students = "student1" to "student2",
            sha = sha1 to sha2,
            gitService = gitService,
            lines = 14,
            percentage = 66,
            repo = testRepoName,
            matchedLines = listOf(
                MatchedLines(
                    match1 = 7 to 19,
                    match2 = 7 to 20,
                    files = testFileName to testFileName
                )
            )
        ),
        AnalysisResult(
            students = "student2" to "student3",
            sha = sha2 to sha3,
            gitService = gitService,
            lines = 5,
            percentage = 22,
            repo = testRepoName,
            matchedLines = listOf(
                MatchedLines(
                    match1 = 16 to 20,
                    match2 = 12 to 16,
                    files = testFileName to testFileName
                )
            )
        ),
        AnalysisResult(
            students = "student1" to "student3",
            sha = sha1 to sha3,
            gitService = gitService,
            lines = 5,
            percentage = 22,
            repo = testRepoName,
            matchedLines = listOf(
                MatchedLines(
                    match1 = 15 to 19,
                    match2 = 12 to 16,
                    files = testFileName to testFileName
                )
            )
        )
    )
}