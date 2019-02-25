package ru.nikstep.redink.core.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.results.AnalysisResultViewBuilder

@RestController
class ResultsController(private val analysisResultViewBuilder: AnalysisResultViewBuilder) {

    @GetMapping(name = "/data/{id}")
    fun getData(@PathVariable("id") id: Int): String {
        return analysisResultViewBuilder.build(id.toLong())
    }

}