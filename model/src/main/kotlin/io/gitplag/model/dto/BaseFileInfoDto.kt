package io.gitplag.model.dto

import io.gitplag.model.entity.BaseFileRecord

/**
 * Base file dto
 */
class BaseFileInfoDto(
    val id: Long,
    val name: String,
    val branch: String
) {
    constructor(baseFileRecord: BaseFileRecord) : this(
        baseFileRecord.id,
        baseFileRecord.fileName,
        baseFileRecord.branch
    )
}