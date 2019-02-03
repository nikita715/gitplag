package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.model.repo.RepositoryRepository

class MossAnalysisService(
    private val solutionLoadingService: SolutionLoadingService,
    private val repositoryRepository: RepositoryRepository,
    private val mossId: String
) : AnalysisService {

    override fun analyse(prData: PullRequestData) {

        prData.changedFiles.forEach {
            val (_, list) = solutionLoadingService.loadSolutions(prData.repoFullName, it)

            val simpleMoss = SimpleMoss(
                mossId,
                "java",
                SocketClient(),
                emptyList(),
                list
            )
            println(simpleMoss.analyse())
        }
    }
}