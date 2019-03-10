package ru.nikstep.redink.model.data

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import ru.nikstep.redink.core.RedinkApplication
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.User
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalysisMode
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language

@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [RedinkApplication::class])
class AnalysisResultRepositoryTest {

    @Autowired
    private lateinit var analysisResultRepository: AnalysisResultRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var repositoryRepository: RepositoryRepository

    private lateinit var repo: Repository

    private lateinit var analysisResult: AnalysisResult

    @Before
    fun setUp() {
        val user = userRepository.save(
            User(
                name = "nikita",
                githubId = 1L,
                installationId = 1L
            )
        )
        repo = repositoryRepository.save(
            Repository(
                owner = user,
                name = "repoName",
                analyser = AnalyserProperty.MOSS,
                analysisMode = AnalysisMode.PERIODIC,
                gitService = GitProperty.GITHUB,
                language = Language.JAVA
            )
        )

        analysisResult = AnalysisResult(
            students = "st1" to "st2",
            sha = "sha1" to "sha2",
            lines = 10,
            percentage = 20,
            repo = repo.name,
            gitService = repo.gitService,
            matchedLines = listOf(MatchedLines(match1 = 1 to 2, match2 = 2 to 3, files = "f1" to "f2"))
        )
    }

    @Test
    fun saveAll() {
        analysisResultRepository.saveAnalysis(repo, listOf(analysisResult))
    }
}