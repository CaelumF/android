package com.toggl.database.properties

import java.time.OffsetDateTime

data class NullableOffsetDateTimeSyncProperty(
    val current: OffsetDateTime?,
    val backup: OffsetDateTime?,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: OffsetDateTime?) = NullableOffsetDateTimeSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun NullableOffsetDateTimeSyncProperty.updateWith(value: OffsetDateTime?): NullableOffsetDateTimeSyncProperty {
    if (current == value) return this
    return when (status) {
        PropertySyncStatus.Syncing,
        PropertySyncStatus.InSync -> copy(
            backup = current,
            current = value,
            status = PropertySyncStatus.SyncNeeded
        )
        PropertySyncStatus.SyncNeeded -> copy(
            current = value,
        )
    }
}
