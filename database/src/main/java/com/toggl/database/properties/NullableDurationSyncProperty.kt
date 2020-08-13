package com.toggl.database.properties

import java.time.Duration

data class NullableDurationSyncProperty(
    val current: Duration?,
    val backup: Duration?,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Duration?) = NullableDurationSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun NullableDurationSyncProperty.updateWith(value: Duration?): NullableDurationSyncProperty {
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
