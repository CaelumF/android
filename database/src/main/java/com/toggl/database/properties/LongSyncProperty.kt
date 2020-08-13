package com.toggl.database.properties

data class LongSyncProperty(
    val current: Long,
    val backup: Long,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Long) = LongSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun LongSyncProperty.updateWith(value: Long): LongSyncProperty {
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
