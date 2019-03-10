package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.entity.Repository

interface AnalysisRepository : JpaRepository<Analysis, Long> {
    fun findFirstByRepositoryOrderByExecutionDateDesc(repository: Repository): Analysis?
}