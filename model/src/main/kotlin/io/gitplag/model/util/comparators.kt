package io.gitplag.model.util

import io.gitplag.gitplagapi.model.output.analysis.AnalyzedPairInfo
import io.gitplag.model.data.AnalysisMatch
import java.util.Comparator

val analysisResultComparator: Comparator<AnalysisMatch> = compareByDescending(AnalysisMatch::percentage)
    .thenComparing { o1, o2 ->
        o1.students.first.toLowerCase().compareTo(o2.students.first.toLowerCase())
    }
    .thenComparing { o1, o2 ->
        o1.students.second.toLowerCase().compareTo(o2.students.second.toLowerCase())
    }

val analysisResultSimplePairDtoComparator: Comparator<AnalyzedPairInfo> =
    compareByDescending(AnalyzedPairInfo::percentage)
        .thenComparing { o1, o2 -> o1.student1.toLowerCase().compareTo(o2.student1.toLowerCase()) }
        .thenComparing { o1, o2 -> o1.student2.toLowerCase().compareTo(o2.student2.toLowerCase()) }