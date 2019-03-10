//package ru.nikstep.redink.core.graphql
//
//import com.coxautodev.graphql.tools.GraphQLQueryResolver
//import ru.nikstep.redink.model.entity.AnalysisPair
//import ru.nikstep.redink.model.repo.AnalysisPairRepository
//
//@Suppress("unused")
//class AnalysisPairQuery(
//    private val analysisPairRepository: AnalysisPairRepository
//) : GraphQLQueryResolver {
//
//    fun analysisPair(repo: String, fileName: String?, student1: String?, student2: String?): List<AnalysisPair?> =
//        analysisPairRepository.run {
//            if (fileName != null) {
//                if (student1 != null) {
//                    if (student2 != null) {
//                        listOf(
//                            findByRepoAndFileNameAndStudent1AndStudent2OrderByIdDesc(
//                                repo,
//                                fileName,
//                                student1,
//                                student2
//                            )
//                        )
//                    }
//                    findAllByRepoAndFileNameAndStudentOrderByIdDesc(repo, fileName, student1)
//                }
//                findAllByRepoAndFileNameOrderByIdDesc(repo, fileName)
//            } else if (student1 != null) {
//                if (student2 != null) {
//                    findAllByRepoAndStudent1AndStudent2OrderByIdDesc(repo, student1, student2)
//                }
//                findAllByRepoAndStudentOrderByIdDesc(repo, student1)
//            }
//            findAllByRepoOrderByIdDesc(repo)
//        }
//
//}
