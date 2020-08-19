package com.toggl.common.feature.navigation

import android.net.Uri
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.toggl.common.DeepLinkUrls
import com.toggl.common.feature.R
import com.toggl.common.feature.models.SelectedCalendarItem
import com.toggl.models.domain.EditableProject
import com.toggl.models.domain.EditableTimeEntry
import com.toggl.models.domain.SettingsType

sealed class Route {
    object Welcome : Route()
    object Login : Route()
    object SignUp : Route()
    object Terms : Route()
    object SsoLink : Route()

    object Timer : Route()
    data class StartEdit(override val parameter: EditableTimeEntry) : Route(), ParameterRoute<EditableTimeEntry>
    data class Project(override val parameter: EditableProject) : Route(), ParameterRoute<EditableProject>

    object Reports : Route()

    object Calendar : Route()
    data class ContextualMenu(override val parameter: SelectedCalendarItem) : Route(), ParameterRoute<SelectedCalendarItem>

    object Settings : Route()
    data class SettingsDialog(override val parameter: SettingsType) : Route(), ParameterRoute<SettingsType>
    object CalendarSettings : Route()
    object Feedback : Route()
    object PasswordReset : Route()
    object About : Route()
    object Licences : Route()
}

interface ParameterRoute<P> {
    val parameter: P
}

fun Route.isSameTypeAs(otherRoute: Route) =
    when (this) {
        Route.Welcome -> otherRoute is Route.Welcome
        Route.Login -> otherRoute is Route.Login
        Route.SignUp -> otherRoute is Route.SignUp
        Route.Terms -> otherRoute is Route.Terms
        Route.SsoLink -> otherRoute is Route.SsoLink
        Route.Timer -> otherRoute is Route.Timer
        Route.Reports -> otherRoute is Route.Reports
        Route.Calendar -> otherRoute is Route.Calendar
        is Route.StartEdit -> otherRoute is Route.StartEdit
        is Route.Project -> otherRoute is Route.Project
        is Route.ContextualMenu -> otherRoute is Route.ContextualMenu
        Route.Settings -> otherRoute is Route.Settings
        is Route.SettingsDialog -> otherRoute is Route.SettingsDialog
        Route.CalendarSettings -> otherRoute is Route.CalendarSettings
        Route.Feedback -> otherRoute is Route.Feedback
        Route.PasswordReset -> otherRoute is Route.PasswordReset
        Route.About -> otherRoute is Route.About
        Route.Licences -> otherRoute is Route.Licences
    }

private val defaultOptions = navOptions {
    anim {
        enter = R.anim.fragment_open_enter
        exit = R.anim.fragment_open_exit
        popEnter = R.anim.fragment_close_enter
        popExit = R.anim.fragment_close_exit
    }
}

private val reportsOptions = navOptions {
    anim {
        enter = R.anim.fragment_open_enter
        exit = R.anim.fragment_open_exit
        popEnter = R.anim.fragment_open_enter
        popExit = R.anim.fragment_open_exit
    }
}

private val calendarOptions = navOptions {
    anim {
        popEnter = R.anim.fragment_open_enter
        popExit = R.anim.fragment_open_exit
    }
}

private val rootFragmentOptions = navOptions {
    launchSingleTop = true
    anim {
        enter = R.anim.fragment_open_enter
        exit = R.anim.fragment_open_exit
        popEnter = R.anim.fragment_close_enter
        popExit = R.anim.fragment_close_exit
    }
}

fun Route.navigationOptions(): NavOptions? =
    when (this) {
        Route.Welcome -> rootFragmentOptions
        Route.Login -> defaultOptions
        Route.SignUp -> defaultOptions
        Route.Terms -> defaultOptions
        Route.PasswordReset -> defaultOptions
        Route.SsoLink -> defaultOptions
        is Route.Project -> null
        Route.Timer -> rootFragmentOptions
        is Route.StartEdit -> null
        Route.Reports -> reportsOptions
        Route.Calendar -> calendarOptions
        is Route.ContextualMenu -> null
        Route.Settings -> defaultOptions
        is Route.SettingsDialog -> null
        Route.CalendarSettings -> defaultOptions
        Route.Feedback -> defaultOptions
        Route.About -> defaultOptions
        Route.Licences -> defaultOptions
    }

fun Route.deepLink(deepLinks: DeepLinkUrls): Uri =
    when (this) {
        Route.Welcome -> deepLinks.welcome
        Route.Login -> deepLinks.login
        Route.SignUp -> deepLinks.signUp
        Route.Terms -> deepLinks.terms
        Route.SsoLink -> deepLinks.ssoLink
        Route.Timer -> deepLinks.timeEntriesLog
        Route.Reports -> deepLinks.reports
        Route.Calendar -> deepLinks.calendar
        is Route.StartEdit -> deepLinks.startEditDialog
        is Route.Project -> deepLinks.projectDialog
        is Route.ContextualMenu -> deepLinks.contextualMenu
        is Route.SettingsDialog -> deepLinks.singleChoiceDialog
        Route.Settings -> deepLinks.settings
        Route.CalendarSettings -> deepLinks.calendarSettings
        Route.Feedback -> deepLinks.submitFeedback
        Route.PasswordReset -> deepLinks.passwordReset
        Route.About -> deepLinks.about
        Route.Licences -> deepLinks.licences
    }
