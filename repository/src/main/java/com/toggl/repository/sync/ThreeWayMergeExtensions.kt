package com.toggl.repository.sync

import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.DurationSyncProperty
import com.toggl.database.properties.IntSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.NullableBooleanSyncProperty
import com.toggl.database.properties.NullableDurationSyncProperty
import com.toggl.database.properties.NullableIntSyncProperty
import com.toggl.database.properties.NullableLongSyncProperty
import com.toggl.database.properties.NullableOffsetDateTimeSyncProperty
import com.toggl.database.properties.NullableStringSyncProperty
import com.toggl.database.properties.OffsetDateTimeSyncProperty
import com.toggl.database.properties.PropertySyncStatus
import com.toggl.database.properties.StringSyncProperty
import java.time.Duration
import java.time.OffsetDateTime

data class PropertyResolutionResult<out T>(val status: PropertySyncStatus, val value: T)

fun BooleanSyncProperty.resolve(server: Boolean) = resolve(status, current, backup, server)
fun DurationSyncProperty.resolve(server: Duration) = resolve(status, current, backup, server)
fun IntSyncProperty.resolve(server: Int) = resolve(status, current, backup, server)
fun LongSyncProperty.resolve(server: Long) = resolve(status, current, backup, server)
fun NullableBooleanSyncProperty.resolve(server: Boolean?) = resolve(status, current, backup, server)
fun NullableDurationSyncProperty.resolve(server: Duration?) = resolve(status, current, backup, server)
fun NullableIntSyncProperty.resolve(server: Int?) = resolve(status, current, backup, server)
fun NullableLongSyncProperty.resolve(server: Long?) = resolve(status, current, backup, server)
fun NullableOffsetDateTimeSyncProperty.resolve(server: OffsetDateTime?) = resolve(status, current, backup, server)
fun NullableStringSyncProperty.resolve(server: String?) = resolve(status, current, backup, server)
fun OffsetDateTimeSyncProperty.resolve(server: OffsetDateTime) = resolve(status, current, backup, server)
fun StringSyncProperty.resolve(server: String): PropertyResolutionResult<String> = resolve(status, current, backup, server)

private fun <T> resolve(status: PropertySyncStatus, current: T, backup: T, server: T): PropertyResolutionResult<T> {
    val original = if (status == PropertySyncStatus.InSync) current else backup
    val resolvedValue = merge(original, current, server)
    val resolvedStatus = if (server == resolvedValue) PropertySyncStatus.InSync else PropertySyncStatus.SyncNeeded
    return PropertyResolutionResult(resolvedStatus, resolvedValue)
}

private fun <T> merge(original: T, local: T, server: T) = when {
    local == original -> server
    server == original -> local
    else -> server
}
