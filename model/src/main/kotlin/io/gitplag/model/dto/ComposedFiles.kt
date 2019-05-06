package io.gitplag.model.dto

import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.SolutionFileRecord

data class ComposedFiles(val bases: List<BaseFileRecord>, val solutions: List<SolutionFileRecord>)