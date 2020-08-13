package com.toggl.database.properties

data class StringSyncProperty(
    val current: String,
    val backup: String,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: String) = StringSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun StringSyncProperty.updateWith(value: String): StringSyncProperty {
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
