package ru.nikstep.redink.analysis

import ru.nikstep.redink.model.entity.PullRequest
import java.util.concurrent.Callable

class AnalysisCallable : Callable<PullRequest> {
    override fun call(): PullRequest {
        TODO("not implemented")
    }

}