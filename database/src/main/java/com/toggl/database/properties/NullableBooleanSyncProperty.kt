package com.toggl.database.properties

data class NullableBooleanSyncProperty(
    val current: Boolean?,
    val backup: Boolean?,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Boolean?) = NullableBooleanSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun NullableBooleanSyncProperty.updateWith(value: Boolean?): NullableBooleanSyncProperty {
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
