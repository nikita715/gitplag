package ru.nikstep.redink.service

import ru.nikstep.redink.data.PullRequestData

interface PlagiarismService {
    fun analyze(data: PullRequestData)
}
