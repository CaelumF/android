package com.toggl.settings.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SettingsSectionViewModel

@Composable
fun SectionList(
    sectionsList: List<SettingsSectionViewModel>,
    titleMode: SectionTitleMode,
    dispatcher: (SettingsAction) -> Unit,
    navigationBarHeight: Dp
) {
    val lastSection = sectionsList.lastOrNull()
    val firstSection = sectionsList.firstOrNull()
    LazyColumnFor(sectionsList) { section ->

        val bottomPadding = if (section == lastSection) navigationBarHeight else 0.dp
        val withTitle = titleMode == SectionTitleMode.All || (titleMode == SectionTitleMode.AllButFirst && section != firstSection)

        Section(
            section = section,
            dispatcher = dispatcher,
            withTitle = withTitle,
            modifier = Modifier.padding(bottom = bottomPadding)
        )
    }
}

enum class SectionTitleMode {
    All,
    AllButFirst,
    None,
}
