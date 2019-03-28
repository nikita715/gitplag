package ru.nikstep.redink.core.beans

import org.springframework.context.support.beans
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.solutions.FileSystemSourceCodeStorage
import ru.nikstep.redink.core.util.safeEnvVar
import ru.nikstep.redink.git.rest.BitbucketRestManager
import ru.nikstep.redink.git.rest.GitRestManager
import ru.nikstep.redink.git.rest.GithubRestManager
import ru.nikstep.redink.git.rest.GitlabRestManager
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
    bean { MossAnalyser(ref(), env.safeEnvVar("redink.mossPath")) }
    bean {
        JPlagAnalyser(
            ref(),
            ref(),
            ref(),
            env.safeEnvVar("redink.solutionsDir"),
            env.safeEnvVar("redink.jplagResultDir")
        )
    }

    bean("analysers") {
        mapOf(
            AnalyserProperty.MOSS to ref<MossAnalyser>(),
            AnalyserProperty.JPLAG to ref<JPlagAnalyser>()
        )
    }

    bean { AnalysisRunner(ref("analysers"), ref()) }
    bean { FileSystemSourceCodeStorage(ref(), ref(), ref(), ref(), env.safeEnvVar("redink.solutionsDir")) }

}
