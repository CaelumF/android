package com.toggl.settings.domain

import com.toggl.models.domain.SettingsType

sealed class SettingsViewModel {
    abstract val label: String
    abstract val settingsType: SettingsType

    data class Toggle(override val label: String, override val settingsType: SettingsType, val toggled: Boolean) : SettingsViewModel()
    data class InfoText(override val label: String, override val settingsType: SettingsType) : SettingsViewModel()
    data class ListChoice(override val label: String, override val settingsType: SettingsType, val selectedValueTitle: String) : SettingsViewModel()
    data class SubPage(override val label: String, override val settingsType: SettingsType) : SettingsViewModel()
    data class ActionRow(override val label: String, override val settingsType: SettingsType) : SettingsViewModel()
}

data class SettingsSectionViewModel(val title: String, val settingsOptions: List<SettingsViewModel>)
