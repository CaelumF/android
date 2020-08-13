package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiPreferencesJsonAdapter
import org.junit.jupiter.api.Test

class ApiPreferencesAdapter {
    @Test
    fun `the ApiPreferences is properly serialized and deserialized`() {
        val adapter = ApiPreferencesJsonAdapter(Moshi.Builder().add(OffsetDateTimeAdapter()).build())
        testJsonAdapter(
            """{"timeofday_format":"H:mm","date_format":"DD.MM.YYYY","duration_format":"decimal","CollapseTimeEntries":true}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
