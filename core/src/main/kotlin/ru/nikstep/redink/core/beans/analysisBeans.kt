package ru.nikstep.redink.core.beans

import org.springframework.context.support.beans
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorage
import ru.nikstep.redink.core.util.safeEnvVar
import ru.nikstep.redink.git.loader.BitbucketLoader
import ru.nikstep.redink.git.loader.GitLoader
import ru.nikstep.redink.git.loader.GithubLoader
import ru.nikstep.redink.git.loader.GitlabLoader
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty

val analysisBeans = beans {

    // Loaders
    bean<GithubLoader>()
    bean<BitbucketLoader>()
    bean<GitlabLoader>()
    bean<Map<GitProperty, GitLoader>>("gitLoaders") {
        mapOf(
            GitProperty.GITHUB to ref<GithubLoader>(),
            GitProperty.BITBUCKET to ref<BitbucketLoader>(),
            GitProperty.GITLAB to ref<GitlabLoader>()
        )
    }

    // Analysers
    bean { MossAnalyser(ref(), env.safeEnvVar("redink.mossId")) }
    bean {
        JPlagAnalyser(
            ref(),
            ref(),
            ref(),
            env.safeEnvVar("redink.solutionsDir"),
            env.safeEnvVar("redink.jplagResultDir"),
            env.safeEnvVar("redink.serverUrl")
        )
    }

    bean("analysers") {
        mapOf(
            AnalyserProperty.MOSS to ref<MossAnalyser>(),
            AnalyserProperty.JPLAG to ref<JPlagAnalyser>()
        )
    }

    bean { AnalysisRunner(ref(), ref("analysers")) }
    bean { FileSystemSolutionStorage(ref(), ref(), env.safeEnvVar("redink.solutionsDir")) }

}
