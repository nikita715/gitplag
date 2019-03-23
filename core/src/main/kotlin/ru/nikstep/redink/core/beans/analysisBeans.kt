package ru.nikstep.redink.core.beans

import org.springframework.context.support.beans
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorage
import ru.nikstep.redink.core.util.safeEnvVar
import ru.nikstep.redink.git.loader.BitbucketRestManager
import ru.nikstep.redink.git.loader.GitRestManager
import ru.nikstep.redink.git.loader.GithubRestManager
import ru.nikstep.redink.git.loader.GitlabRestManager
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.GitProperty

val analysisBeans = beans {

    // Loaders
    bean<GithubRestManager>()
    bean<BitbucketRestManager>()
    bean<GitlabRestManager>()
    bean<Map<GitProperty, GitRestManager>>("gitRestManagers") {
        mapOf(
            GitProperty.GITHUB to ref<GithubRestManager>(),
            GitProperty.BITBUCKET to ref<BitbucketRestManager>(),
            GitProperty.GITLAB to ref<GitlabRestManager>()
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

    bean { AnalysisRunner(ref("analysers"), ref()) }
    bean { FileSystemSolutionStorage(ref(), ref(), ref(), ref(), env.safeEnvVar("redink.solutionsDir")) }

}
