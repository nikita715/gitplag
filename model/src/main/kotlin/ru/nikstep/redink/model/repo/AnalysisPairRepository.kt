package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.AnalysisPair

interface AnalysisPairRepository : JpaRepository<AnalysisPair, Long>