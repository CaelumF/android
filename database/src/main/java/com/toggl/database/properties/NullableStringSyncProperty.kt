package com.toggl.database.properties

data class NullableStringSyncProperty(
    val current: String?,
    val backup: String?,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: String?) = NullableStringSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun NullableStringSyncProperty.updateWith(value: String?): NullableStringSyncProperty {
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
