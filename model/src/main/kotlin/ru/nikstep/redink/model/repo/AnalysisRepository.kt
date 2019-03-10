package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.Analysis

interface AnalysisRepository : JpaRepository<Analysis, Int>