package com.toggl.database.properties

import java.time.Duration

data class DurationSyncProperty(
    val current: Duration,
    val backup: Duration,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Duration) = DurationSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun DurationSyncProperty.updateWith(value: Duration): DurationSyncProperty {
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
