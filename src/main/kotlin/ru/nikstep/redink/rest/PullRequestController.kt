package ru.nikstep.redink.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.service.PullRequestSavingService
import javax.servlet.http.HttpServletRequest

@RestController
class PullRequestController(val pullRequestSavingService: PullRequestSavingService) {

    @PostMapping(name = "/pull", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun get(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        when (httpServletRequest.getHeader("X-GitHub-Event")) {
            "pull_request" -> pullRequestSavingService.storePullRequest(payload)
        }
    }
}
