package com.toggl.sync

import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.PropertySyncStatus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The Three way merge extensions")
class ThreeWayMergeExtensionsTests {

    @Test
    fun `returns the server value if no changes were made locally`() {
        val localValue = 1L
        val serverValue = 2L
        val syncProperty = LongSyncProperty(localValue, localValue, PropertySyncStatus.InSync)
        val result = syncProperty.resolve(serverValue)

        result.value shouldBe serverValue
    }

    @Test
    fun `returns the local value if changes were made locally but not on the server`() {
        val originalValue = 1L
        val localValue = 2L
        val serverValue = 1L
        val syncProperty = LongSyncProperty(localValue, originalValue, PropertySyncStatus.SyncNeeded)
        val result = syncProperty.resolve(serverValue)

        result.value shouldBe localValue
    }

    @Test
    fun `returns the server value if changes were made both locally and on the server`() {
        val originalValue = 1L
        val localValue = 2L
        val serverValue = 3L
        val syncProperty = LongSyncProperty(localValue, originalValue, PropertySyncStatus.SyncNeeded)
        val result = syncProperty.resolve(serverValue)

        result.value shouldBe serverValue
    }

    @Test
    fun `returns in sync if no changes were made locally`() {
        val localValue = 1L
        val serverValue = 2L
        val syncProperty = LongSyncProperty(localValue, localValue, PropertySyncStatus.InSync)
        val result = syncProperty.resolve(serverValue)

        result.status shouldBe PropertySyncStatus.InSync
    }

    @Test
    fun `returns sync needed if changes were made locally but not on the server`() {
        val originalValue = 1L
        val localValue = 2L
        val serverValue = 1L
        val syncProperty = LongSyncProperty(localValue, originalValue, PropertySyncStatus.SyncNeeded)
        val result = syncProperty.resolve(serverValue)

        result.status shouldBe PropertySyncStatus.SyncNeeded
    }

    @Test
    fun `returns in sync if changes were made both locally and on the server`() {
        val originalValue = 1L
        val localValue = 2L
        val serverValue = 3L
        val syncProperty = LongSyncProperty(localValue, originalValue, PropertySyncStatus.SyncNeeded)
        val result = syncProperty.resolve(serverValue)

        result.status shouldBe PropertySyncStatus.InSync
    }
}
