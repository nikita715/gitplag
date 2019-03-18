//package ru.nikstep.redink.core.view
//
//import kotlinx.html.*
//import kotlinx.html.stream.createHTML
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.RestController
//import ru.nikstep.redink.git.loader.GitLoader
//import ru.nikstep.redink.model.repo.AnalysisPairRepository
//import ru.nikstep.redink.util.GitProperty
//
///**
// * Analysis results views controller
// */
//@RestController
//class ResultsController(
//    private val analysisPairRepository: AnalysisPairRepository,
//    @Qualifier("gitLoaders") private val loaders: Map<GitProperty, GitLoader>
//) {
//
//    private val resultsStyle: HEAD.() -> Unit = {
//        styleLink("/style.css")
//        link(href = "https://fonts.googleapis.com/css?family=Roboto", rel = LinkRel.stylesheet)
//    }
//
//    /**
//     * Get two solutions on one page
//     */
//    @GetMapping("result/{id}")
//    fun getResult(@PathVariable id: Int): String {
//        val analysisPair = analysisPairRepository.findById(id.toLong()).get()
//        val file1 = ""
////            loaders.getValue(analysisPair.gitService)
////            .loadFileText(analysisPair.repo, analysisPair.student1Sha, analysisPair.analysisPairLines[0].fileName1)
//        val file2 = ""
////            loaders.getValue(analysisPair.gitService)
////            .loadFileText(analysisPair.repo, analysisPair.student2Sha, analysisPair.analysisPairLines[0].fileName2)
//
//        return createHTML().html {
//            head {
//                title("Result #${analysisPair.id}")
//                apply(resultsStyle)
//            }
//            body {
//                header("solution-compare-header") {
//                    span("student-name") { +analysisPair.student1 }
//                    span("student-name") { +analysisPair.student2 }
//                }
//                main("solution-compare") {
//                    section("solution") {
//                        pre {
//                            +file1
//                        }
//                    }
//                    section("solution") {
//                        pre {
//                            +file2
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Get list of analysis results by the [repoName]
//     */
//    @GetMapping("result/{repoOwner}/{repoName}")
//    fun getResultsOfRepository(@PathVariable repoOwner: String, @PathVariable repoName: String): String = createHTML()
//        .html {
//            val repoFullName = "$repoOwner/$repoName"
//            head {
//                title("Results of $repoFullName")
//                apply(resultsStyle)
//            }
//            body {
//                analysisPairRepository.findAllByRepoOrderByIdDesc(repoFullName).forEach { analysisPair ->
//                    a("/result/${analysisPair.id}") { +analysisPair.id.toString() }
//                    br { }
//                }
//            }
//        }
//
//    /**
//     * Get list of analysis results by the [repoName] and [studentName]
//     */
//    @GetMapping("result/{repoOwner}/{repoName}/{studentName}")
//    fun getResultsOfRepositoryAndStudent(
//        @PathVariable repoOwner: String,
//        @PathVariable repoName: String,
//        @PathVariable studentName: String
//    ): String = createHTML()
//        .html {
//            val repoFullName = "$repoOwner/$repoName"
//            head {
//                title("Results of $repoFullName")
//                apply(resultsStyle)
//            }
//            body {
//                analysisPairRepository.findAllByRepoAndStudentOrderByIdDesc(repoFullName, studentName)
//                    .forEach { analysisPair ->
//                        a("/result/${analysisPair.id}") { +analysisPair.id.toString() }
//                    }
//            }
//        }
//
//}