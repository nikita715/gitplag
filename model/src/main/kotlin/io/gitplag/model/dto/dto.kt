package io.gitplag.model.dto

import io.gitplag.gitplagapi.model.enums.AnalyzerProperty
import io.gitplag.gitplagapi.model.input.AnalysisRequest
import io.gitplag.gitplagapi.model.input.FileSearchCriteria
import io.gitplag.gitplagapi.model.input.RepositoryInput
import io.gitplag.gitplagapi.model.output.analysis.AnalysisResult
import io.gitplag.gitplagapi.model.output.analysis.AnalyzedPairInfo
import io.gitplag.gitplagapi.model.output.analysis.pair.AnalysedPair
import io.gitplag.gitplagapi.model.output.analysis.pair.AnalysedPairContent
import io.gitplag.gitplagapi.model.output.analysis.pair.AnalyzedPairMatch
import io.gitplag.gitplagapi.model.output.file.content.FileContent
import io.gitplag.gitplagapi.model.output.file.info.BaseBranchInfo
import io.gitplag.gitplagapi.model.output.file.info.FileInfo
import io.gitplag.gitplagapi.model.output.file.info.RepositoryFilesInfo
import io.gitplag.gitplagapi.model.output.file.info.SolutionBranchInfo
import io.gitplag.gitplagapi.model.output.file.info.SolutionInfo
import io.gitplag.gitplagapi.model.output.repository.RepositoryOutput
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.AnalysisPair
import io.gitplag.model.entity.AnalysisPairLines
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.util.analysisResultSimplePairDtoComparator
import io.gitplag.gitplagapi.model.input.RepositoryUpdate as RepositoryUpdateModel
import io.gitplag.gitplagapi.model.output.pullrequest.PullRequest as PullRequestDto

typealias InputRepositoryDto = RepositoryInput
typealias RepositoryUpdate = RepositoryUpdateModel
typealias FileDto = FileContent
typealias AnalysisFilePairDto = AnalysedPairContent
typealias AnalysisDto = AnalysisRequest
typealias BaseBranchInfoDto = BaseBranchInfo
typealias LocalFileDto = FileSearchCriteria
typealias RepositoryFilesInfoDto = RepositoryFilesInfo
typealias SolutionBranchInfoDto = SolutionBranchInfo
typealias StudentFilesDto = SolutionInfo
typealias OutputRepositoryDto = RepositoryOutput
typealias AnalysisResultDto = AnalysisResult

fun AnalysisPairDto(analysisPair: AnalysisPair, analyzer: AnalyzerProperty) = AnalysedPair(
    analysisPair.id,
    analysisPair.student1,
    analysisPair.student2,
    analysisPair.percentage,
    analysisPair.minPercentage,
    analysisPair.maxPercentage,
    analysisPair.createdAt1,
    analysisPair.createdAt2,
    analysisPair.analysisPairLines.filter { it.analyzer == analyzer }.map { AnalysisPairLinesDto(it) }.sortedBy { it.from1 }
)

fun AnalysisPairLinesDto(analysisPairLines: AnalysisPairLines) = AnalyzedPairMatch(
    analysisPairLines.id, analysisPairLines.from1, analysisPairLines.to1,
    analysisPairLines.from2, analysisPairLines.to2,
    analysisPairLines.fileName1, analysisPairLines.fileName2
)

fun AnalysisResultDto(analysis: Analysis) = AnalysisResult(
    analysis.id, analysis.repository.id, analysis.repository.name, analysis.analyzer,
    analysis.branch, analysis.executionDate, analysis.resultLink,
    analysis.analysisPairs.map { AnalysisResultSimplePairDto(it) }
        .sortedWith(analysisResultSimplePairDtoComparator),
    analysis.studentsWithoutSolutions
)

fun AnalysisResultSimplePairDto(analysisPair: AnalysisPair) = AnalyzedPairInfo(
    analysisPair.id,
    analysisPair.student1,
    analysisPair.student2,
    analysisPair.percentage,
    analysisPair.minPercentage,
    analysisPair.maxPercentage,
    analysisPair.createdAt1,
    analysisPair.createdAt2
)

fun FileInfoDto(baseFileRecord: BaseFileRecord) = FileInfo(
    baseFileRecord.id,
    baseFileRecord.fileName
)

fun FileInfoDto(solutionFileRecord: SolutionFileRecord) = FileInfo(
    solutionFileRecord.id,
    solutionFileRecord.fileName
)

fun OutputRepositoryDto(repo: Repository) = RepositoryOutput(
    id = repo.id,
    filePatterns = repo.filePatterns,
    name = repo.name,
    analyzer = repo.analyzer,
    gitService = repo.gitService,
    language = repo.language,
    analysisMode = repo.analysisMode,
    autoCloningEnabled = repo.autoCloningEnabled
)

fun PullRequestDto(pullRequest: PullRequest) = PullRequestDto(
    pullRequest.id,
    pullRequest.number,
    pullRequest.creatorName,
    pullRequest.sourceBranchName,
    pullRequest.mainBranchName
)
