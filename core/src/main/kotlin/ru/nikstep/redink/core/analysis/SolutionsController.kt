package ru.nikstep.redink.core.analysis

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.GitProperty

@RestController
class SolutionsController(
    private val sourceCodeRepository: SourceCodeRepository
) {

    @GetMapping("/solutions")
    fun solutions(
        @RequestParam("git") git: String,
        @RequestParam("repo") repo: String,
        @RequestParam("sourceBranch", required = false) sourceBranch: String?,
        @RequestParam("targetBranch", required = false) targetBranch: String?,
        @RequestParam("student", required = false) student: String?,
        @RequestParam("fileName", required = false) fileName: String?
    ) = sourceCodeRepository.findAllByGitServiceAndRepo(GitProperty.valueOf(git.toUpperCase()), repo)
        .filter { if (sourceBranch != null) it.sourceBranch == sourceBranch else true }
        .filter { if (targetBranch != null) it.targetBranch == targetBranch else true }
        .filter { if (student != null) it.user == student else true }
        .filter { if (fileName != null) it.fileName == fileName else true }

}