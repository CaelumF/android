package com.toggl.database.properties

data class NullableIntSyncProperty(
    val current: Int?,
    val backup: Int?,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Int?) = NullableIntSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun NullableIntSyncProperty.updateWith(value: Int?): NullableIntSyncProperty {
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
