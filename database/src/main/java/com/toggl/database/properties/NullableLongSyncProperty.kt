package com.toggl.database.properties

data class NullableLongSyncProperty(
    val current: Long?,
    val backup: Long?,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Long?) = NullableLongSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun NullableLongSyncProperty.updateWith(value: Long?): NullableLongSyncProperty {
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
