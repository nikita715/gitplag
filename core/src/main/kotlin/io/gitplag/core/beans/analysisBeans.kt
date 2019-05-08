package io.gitplag.core.beans

import io.gitplag.analysis.analyzer.JPlagAnalyzer
import io.gitplag.analysis.analyzer.MossAnalyzer
import io.gitplag.analysis.solutions.FileSystemSourceCodeStorage
import io.gitplag.core.util.safeEnvVar
import io.gitplag.git.GitAnalysisRunner
import io.gitplag.model.enums.AnalyzerProperty
import org.springframework.context.support.beans

val analysisBeans = beans {

    // Analyzers
    bean {
        MossAnalyzer(
            ref(), ref(),
            env.safeEnvVar("gitplag.analysisFilesDir"),
            env.safeEnvVar("gitplag.mossId")
        )
    }
    bean {
        JPlagAnalyzer(
            ref(), ref(), ref(),
            env.safeEnvVar("gitplag.analysisFilesDir"),
            env.safeEnvVar("gitplag.jplagResultDir")
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
        FileSystemSourceCodeStorage(ref(), ref(), ref(), ref(), env.safeEnvVar("gitplag.solutionsDir"))
    }

}
