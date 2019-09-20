package io.gitplag.core.beans

import io.gitplag.analysis.analyzer.CombinedAnalyzer
import io.gitplag.analysis.analyzer.JPlagAnalyzer
import io.gitplag.analysis.analyzer.MossAnalyzer
import io.gitplag.analysis.solutions.FileSystemSourceCodeStorage
import io.gitplag.core.util.safeEnvVar
import io.gitplag.git.GitAnalysisRunner
import io.gitplag.model.enums.AnalyzerProperty
import org.springframework.context.support.beans

val analysisBeans = beans {

    val gitplag = "gitplag"

    val mossId = env.safeEnvVar("$gitplag.mossId")
    val jplagResultDir = env.safeEnvVar("$gitplag.jplagResultDir")
    val solutionsDir = env.safeEnvVar("$gitplag.solutionsDir")
    val analysisFilesDir = env.safeEnvVar("$gitplag.analysisFilesDir")

    // Analyzers
    bean {
        MossAnalyzer(
            ref(),
            analysisFilesDir,
            mossId
        )
    }
    bean {
        JPlagAnalyzer(
            ref(),
            analysisFilesDir,
            jplagResultDir
        )
    }

    bean("standaloneAnalyzers") {
        mapOf(
            AnalyzerProperty.MOSS to ref<MossAnalyzer>(),
            AnalyzerProperty.JPLAG to ref<JPlagAnalyzer>()
        )
    }

    bean {
        CombinedAnalyzer(
            ref("standaloneAnalyzers"), ref(),
            analysisFilesDir
        )
    }

    bean("analyzers") {
        mapOf(
            AnalyzerProperty.MOSS to ref<MossAnalyzer>(),
            AnalyzerProperty.JPLAG to ref<JPlagAnalyzer>(),
            AnalyzerProperty.COMBINED to ref<CombinedAnalyzer>()
        )
    }

    bean { GitAnalysisRunner(ref("analyzers"), ref("payloadProcessors"), ref(), ref()) }
    bean {
        FileSystemSourceCodeStorage(
            ref(), ref(), ref(), ref(),
            solutionsDir,
            jplagResultDir,
            analysisFilesDir
        )
    }

}
