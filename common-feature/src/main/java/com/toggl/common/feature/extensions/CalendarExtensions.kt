package com.toggl.common.feature.extensions

import com.toggl.common.feature.services.calendar.Calendar

fun Collection<Calendar>.getEnabledCalendars(enabledIds: List<String>) = this.filter { it.id in enabledIds }
