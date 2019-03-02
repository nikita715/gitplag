package ru.nikstep.redink.github

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import ru.nikstep.redink.github.temporary.ChangeLoader
import ru.nikstep.redink.util.asPath
import java.nio.file.Paths


private val relSolutionsDir = asPath("src", "test", "resources", "payload")

internal fun readPayloadOf(gitService: String): String =
    Paths.get(relSolutionsDir, "$gitService.json").toFile().readText()

internal val changeLoader = mock<ChangeLoader> {
    on { loadChanges(any(), any(), any(), any(), any()) } doReturn listOf()
}
