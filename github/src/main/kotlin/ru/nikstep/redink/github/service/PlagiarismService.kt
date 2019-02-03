package ru.nikstep.redink.github.service

import ru.nikstep.redink.data.PullRequestData

interface PlagiarismService {
    fun analyze(data: PullRequestData)
}
