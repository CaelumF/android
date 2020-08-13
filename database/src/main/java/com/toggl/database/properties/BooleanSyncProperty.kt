package com.toggl.database.properties

data class BooleanSyncProperty(
    val current: Boolean,
    val backup: Boolean,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Boolean) = BooleanSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun BooleanSyncProperty.updateWith(value: Boolean): BooleanSyncProperty {
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
