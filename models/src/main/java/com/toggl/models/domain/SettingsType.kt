package com.toggl.models.domain

sealed class SettingsType {
    sealed class TextSetting : SettingsType() {
        object Name : TextSetting()
        object Email : TextSetting()
    }
    open class SingleChoiceSetting : SettingsType()

    object Workspace : SingleChoiceSetting()
    object DateFormat : SingleChoiceSetting()
    object TwentyFourHourClock : SettingsType()
    object DurationFormat : SingleChoiceSetting()
    object FirstDayOfTheWeek : SingleChoiceSetting()
    object GroupSimilar : SettingsType()
    object CellSwipe : SettingsType()
    object ManualMode : SettingsType()
    object CalendarSettings : SettingsType()
    object AllowCalendarAccess : SettingsType()
    object CalendarPermissionInfo : SettingsType()
    data class Calendar(val name: String, val id: String, val enabled: Boolean) : SettingsType()
    object SmartAlert : SingleChoiceSetting()
    object SubmitFeedback : SettingsType()
    object About : SettingsType()
    object PrivacyPolicy : SettingsType()
    object TermsOfService : SettingsType()
    object Licenses : SettingsType()
    object Help : SettingsType()
    object SignOut : SettingsType()

    // debug sections
    object InsertMockData : SettingsType()
}
