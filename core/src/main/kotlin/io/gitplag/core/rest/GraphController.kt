package io.gitplag.core.rest

import io.gitplag.model.data.graph.Direction
import io.gitplag.model.data.graph.GraphData
import io.gitplag.model.data.graph.Link
import io.gitplag.model.data.graph.Node
import io.gitplag.model.entity.AnalysisPair
import io.gitplag.model.repo.AnalysisRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for graph representation
 */
@RestController
class GraphController(
    private val analysisRepository: AnalysisRepository,
    @Value("\${gitplag.graphUrl}") private val graphUrl: String,
    @Value("\${gitplag.serverUrl}") private val serverUrl: String
) {

    /**
     * Get analysis result data for graph
     */
    @GetMapping("/graph/{analysisId}")
    fun graphData(@PathVariable analysisId: Long): GraphData {
        val analysisPairs = analysisRepository.findById(analysisId).get().analysisPairs
        return extractGraphData(analysisPairs, analysisId)
    }

    /**
     * Get analysis result data for graph by the [studentName]
     */
    @GetMapping("/graph/{analysisId}/student/{studentName}")
    fun graphDataForOneStudent(@PathVariable analysisId: Long, @PathVariable studentName: String): GraphData {
        val analysisPairs = analysisRepository.findById(analysisId).get().analysisPairs.filter {
            it.run { student1 == studentName || student2 == studentName }
        }
        return extractGraphData(analysisPairs, analysisId)
    }

    private fun extractGraphData(
        analysisPairs: List<AnalysisPair>,
        analysisId: Long
    ): GraphData {
        val nameSet = mutableSetOf<String>()
        analysisPairs.forEach { pair ->
            nameSet += pair.student1
            nameSet += pair.student2
        }
        return GraphData(nameSet.map { createGraphNode(analysisId, it) }, analysisPairs.map {
            Link(
                it.student1,
                it.student2,
                it.percentage,
                findDirection(it),
                "$serverUrl/analysis/$analysisId/pair/${it.id}"
            )
        })
    }

    private fun createGraphNode(analysisId: Long, name: String) =
        Node(name, "$graphUrl$serverUrl/graph/$analysisId/student/$name")

    private fun findDirection(analysisPair: AnalysisPair): Direction {
        analysisPair.run { return if (createdAt1 > createdAt2) Direction.FIRST else Direction.SECOND }
    }

}