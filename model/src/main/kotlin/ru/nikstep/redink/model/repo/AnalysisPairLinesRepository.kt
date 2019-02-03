package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.AnalysisPairLines

interface AnalysisPairLinesRepository : JpaRepository<AnalysisPairLines, Long>