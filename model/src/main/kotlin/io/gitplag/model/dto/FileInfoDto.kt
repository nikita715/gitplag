package io.gitplag.model.dto

import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.SolutionFileRecord

/**
 * File info dto
 */
class FileInfoDto(
    val id: Long,
    val name: String
) {
    constructor(baseFileRecord: BaseFileRecord) : this(
        baseFileRecord.id,
        baseFileRecord.fileName
    )

    constructor(solutionFileRecord: SolutionFileRecord) : this(
        solutionFileRecord.id,
        solutionFileRecord.fileName
    )
}