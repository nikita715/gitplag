package io.gitplag.analysis

import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis

/**
 * Main analysis class
 */
interface AnalysisRunner {

    /**
     * Run analysis with [settings]
     */
    fun run(settings: AnalysisSettings): Analysis

    /**
     * Run all analyzes with [settingsList]
     */
    fun run(settingsList: List<AnalysisSettings>): List<Analysis>

}