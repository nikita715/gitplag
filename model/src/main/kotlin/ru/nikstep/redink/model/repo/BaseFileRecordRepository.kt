package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.BaseFileRecord
import ru.nikstep.redink.model.entity.Repository

interface BaseFileRecordRepository : JpaRepository<BaseFileRecord, Long> {

    fun findAllByRepoAndBranch(repo: Repository, branch: String): List<BaseFileRecord>

}