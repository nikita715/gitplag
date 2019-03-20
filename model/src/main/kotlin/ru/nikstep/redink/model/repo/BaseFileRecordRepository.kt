package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.BaseFileRecord
import ru.nikstep.redink.model.entity.Repository

interface BaseFileRecordRepository : JpaRepository<BaseFileRecord, Long> {

    fun findAllByRepoAndBranch(repo: Repository, branch: String): List<BaseFileRecord>

    fun findAllByRepo(repo: Repository): List<BaseFileRecord>

    @Transactional
    fun deleteAllByRepoAndBranch(repo: Repository, branch: String)

}