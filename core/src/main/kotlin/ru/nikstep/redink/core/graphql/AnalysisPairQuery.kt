package ru.nikstep.redink.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.repo.AnalysisPairRepository

@Suppress("unused")
class AnalysisPairQuery(
    private val analysisPairRepository: AnalysisPairRepository
) : GraphQLQueryResolver {

    fun analysisPair(repo: String, fileName: String?, student1: String?, student2: String?): List<AnalysisPair?> =
        analysisPairRepository.run {
            if (fileName != null) {
                if (student1 != null) {
                    if (student2 != null) {
                        listOf(findAllByRepoAndFileNameAndStudent1AndStudent2(repo, fileName, student1, student2))
                    }
                    findAllByRepoAndFileNameAndStudent(repo, fileName, student1)
                }
                findAllByRepoAndFileName(repo, fileName)
            } else if (student1 != null) {
                if (student2 != null) {
                    findAllByRepoAndStudent1AndStudent2(repo, student1, student2)
                }
                findAllByRepoAndStudent(repo, student1)
            }
            findAllByRepo(repo)
        }

}
