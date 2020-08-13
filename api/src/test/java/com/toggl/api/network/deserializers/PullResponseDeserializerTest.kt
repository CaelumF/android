package com.toggl.api.network.deserializers

import com.squareup.moshi.Moshi
import com.toggl.api.common.TestDataUtils
import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import com.toggl.api.network.models.sync.PullResponseJsonAdapter
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test

class PullResponseDeserializerTest {
    @Test
    fun `the pull response is properly serialized`() {
        val pullResponse = TestDataUtils.getPullResponse()
        val moshi = Moshi.Builder().add(OffsetDateTimeAdapter()).build()
        val deserializedPullResponse = PullResponseJsonAdapter(moshi).fromJson(pullResponse)!!
        with(deserializedPullResponse) {
            serverTime > 10
            user.shouldNotBeNull()
            clients.shouldNotBeEmpty()
            projects.shouldNotBeEmpty()
            timeEntries.shouldNotBeEmpty()
            tags.shouldNotBeEmpty()
            tasks.shouldNotBeEmpty()
            preferences.shouldNotBeNull()
        }
    }
}
