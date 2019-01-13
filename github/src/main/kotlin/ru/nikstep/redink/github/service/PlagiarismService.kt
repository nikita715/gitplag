package ru.nikstep.redink.github.service

import ru.nikstep.redink.github.data.PullRequestData

interface PlagiarismService {
    fun analyze(data: PullRequestData)
}
