package ru.nikstep.redink.core.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController("")
class BitbucketWebhookController {

    @PostMapping("/webhook/bitbucket", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processBitbucketWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {

    }
}