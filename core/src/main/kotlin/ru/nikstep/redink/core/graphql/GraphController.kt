package ru.nikstep.redink.core.graphql

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.model.data.graph.Direction
import ru.nikstep.redink.model.data.graph.GraphData
import ru.nikstep.redink.model.data.graph.Link
import ru.nikstep.redink.model.data.graph.Node
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.repo.AnalysisRepository

@RestController
class GraphController(
    private val analysisRepository: AnalysisRepository,
    @Value("\${redink.graphUrl}") private val graphUrl: String,
    @Value("\${redink.serverUrl}") private val serverUrl: String,
    @Value("\${server.port}") private val serverPort: String
) {

    @GetMapping("/graph/{analysisId}")
    fun graphData(@PathVariable analysisId: Long): GraphData {
        val analysisPairs = analysisRepository.findById(analysisId).get().analysisPairs
        return extractGraphData(analysisPairs, analysisId)
    }

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