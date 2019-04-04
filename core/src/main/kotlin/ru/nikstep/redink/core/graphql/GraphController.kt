package ru.nikstep.redink.core.graphql

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.repo.AnalysisRepository

@RestController
class GraphController(private val analysisRepository: AnalysisRepository) {

    @GetMapping("/graph/{analysisId}")
    fun graphData(@PathVariable analysisId: Long): GraphData {
        val analysisPairs = analysisRepository.findById(analysisId).get().analysisPairs
        val nameSet = mutableSetOf<String>()
        analysisPairs.forEach { pair ->
            nameSet += pair.student1
            nameSet += pair.student2
        }
        return GraphData(nameSet.map(::Node), analysisPairs.map {
            Link(it.student1, it.student2, it.percentage, findDirection(it))
        })
    }

    private fun findDirection(analysisPair: AnalysisPair): Direction {
        analysisPair.run { return if (createdAt1 > createdAt2) Direction.FIRST else Direction.SECOND }
    }

}

class GraphData(
    val nodes: Collection<Node>,
    val links: Collection<Link>
)

class Node(
    val name: String
)

class Link(
    val first: String,
    val second: String,
    val weight: Int,
    val directedTo: Direction
)

enum class Direction {
    FIRST,
    SECOND
}