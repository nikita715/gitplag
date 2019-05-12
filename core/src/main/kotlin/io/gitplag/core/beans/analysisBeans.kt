package io.gitplag.core.beans

import io.gitplag.analysis.analyzer.JPlagAnalyzer
import io.gitplag.analysis.analyzer.MossAnalyzer
import io.gitplag.analysis.solutions.FileSystemSourceCodeStorage
import io.gitplag.core.util.safeEnvVar
import io.gitplag.git.GitAnalysisRunner
import io.gitplag.model.enums.AnalyzerProperty
import org.springframework.context.support.beans

val analysisBeans = beans {
    val gitplag = "gitplag"

    // Analyzers
    bean {
        MossAnalyzer(
            ref(),
            env.safeEnvVar("$gitplag.analysisFilesDir"),
            env.safeEnvVar("$gitplag.mossId")
        )
    }
    bean {
        JPlagAnalyzer(
            ref(),
            env.safeEnvVar("$gitplag.analysisFilesDir"),
            env.safeEnvVar("$gitplag.jplagResultDir"),
            env.safeEnvVar("$gitplag.serverUrl")
        )
    }

    bean("analyzers") {
        mapOf(
            AnalyzerProperty.MOSS to ref<MossAnalyzer>(),
            AnalyzerProperty.JPLAG to ref<JPlagAnalyzer>()
        )
    }

    bean { GitAnalysisRunner(ref("analyzers"), ref("payloadProcessors"), ref()) }
    bean {
        FileSystemSourceCodeStorage(
            ref(), ref(), ref(), ref(), env.safeEnvVar("$gitplag.solutionsDir"),
            env.safeEnvVar("$gitplag.jplagResultDir"),
            env.safeEnvVar("$gitplag.analysisFilesDir")
        )
    }

}
